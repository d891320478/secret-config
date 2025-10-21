package main

import (
	"bufio"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"os"
	"runtime/debug"

	"github.com/d891320478/secret-config/init/aes"
	"github.com/d891320478/secret-config/init/fileopt"
	"github.com/d891320478/secret-config/init/utils"
	"gopkg.in/yaml.v3"
)

const work_key_length = 32

type ConfigOpt struct {
	DoEncrypt bool `json:"-" yaml:"-"`
}

type DbConfig struct {
	Driver    string `json:"driver" yaml:"driver"`
	Db        string `json:"db" yaml:"db"`
	Schema    string `json:"schema"  yaml:"schema"`
	User      string `json:"user" yaml:"user"`
	Pass      string `json:"pass" yaml:"pass"`
	Address   string `json:"address" yaml:"address"`
	ConfigOpt `json:"-" yaml:"-"`
}

type RedisConfig struct {
	User      string `json:"user" yaml:"user"`
	Pass      string `json:"pass" yaml:"pass"`
	Address   string `json:"address" yaml:"address"`
	Sentinal  string `json:"sentinal" yaml:"sentinal"`
	ConfigOpt `json:"-" yaml:"-"`
}

// Throwable 是一个用于捕获和打印 panic 信息的函数
// 该函数没有参数和返回值
// 主要用途是在 defer 语句中调用，以捕获程序中的异常并输出详细的堆栈信息
func Throwable() {
	// 恢复 panic 并获取错误信息
	err := recover()
	if err == nil {
		return
	}

	// 打印错误信息和堆栈跟踪
	fmt.Println(err)
	fmt.Println(string(debug.Stack()))
}

// CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o build/create-rt -x main.go

// 这个init工程用于给secret-config生成加密配置文件，包括数据库配置和Redis配置。
// main 是程序的入口函数，负责解析命令行参数、读取配置文件并根据参数决定是否更新或重新生成加密配置。
// 它会处理数据库和 Redis 的 YAML 配置文件，并将其加密后保存为二进制格式。
// 同时支持通过 -update 参数控制是否仅更新配置而不重新生成根密钥与工作密钥。
// 主要流程包括：
// 1. 解析命令行标志（如 -update）；
// 2. 定义相关文件路径；
// 3. 根据是否存在 db.yaml 和 redis.yaml 文件加载并解析配置；
// 4. 若使用 -update 标志，则复用已有根密钥和工作密钥进行配置加密；
// 5. 否则重新生成根密钥、工作密钥，并加密配置；
// 6. 最后删除原始明文配置文件。

func main() {
	defer Throwable()
	// decrypt := flag.Bool("decrypt", false, "true or false，用于解密config_db,config_redis")
	update := flag.Bool("update", false, "true or false，只更新配置文件，不重新生成密钥时为true")
	flag.Parse()

	// 获取当前可执行文件路径作为基础目录
	basePath, err := fileopt.GetExecutableDir()
	if err != nil {
		panic(err)
	}

	// 初始化各个关键文件路径
	rootKeyFile := basePath + "/rtk"
	rootKeySaltFile := basePath + "/rts"
	workKeyFile := basePath + "/work.ey"
	dbConfFile := basePath + "/db.yaml"
	redisConfFile := basePath + "/redis.yaml"
	secretDbFile := basePath + "/config_db"
	secretRedisFile := basePath + "/config_redis"

	// 解密逻辑（已注释）
	// if *decrypt {
	// 	rootKey := hex.EncodeToString(utils.GetRootKey(utils.ReadRt(rootKeyFile, rootKeySaltFile)))
	// 	workKeySecret, err := os.ReadFile(workKeyFile)
	// 	if err != nil {
	// 		panic("read config workkey error, check file permissions or use createRt. err = " + err.Error())
	// 	}
	// 	workKey, err := aes.AESGCMDecrypt(string(workKeySecret), rootKey)
	// 	if err != nil {
	// 		panic("decrypt config workkey error. err = " + err.Error())
	// 	}
	// 	// db
	// 	dbSecret, err := os.ReadFile(secretDbFile)
	// 	if err != nil {
	// 		panic("read config db error, check file permissions or use createRt. err = " + err.Error())
	// 	}
	// 	configJson, err := aes.AESGCMDecrypt(string(dbSecret), hex.EncodeToString(workKey))
	// 	if err != nil {
	// 		panic("decrypt config db error. err = " + err.Error())
	// 	}
	// 	fmt.Println(string(configJson))
	// 	// redis
	// 	redisSecret, err := os.ReadFile(secretRedisFile)
	// 	if err != nil {
	// 		panic("read config redis error, check file permissions or use createRt. err = " + err.Error())
	// 	}
	// 	configJson, err = aes.AESGCMDecrypt(string(redisSecret), hex.EncodeToString(workKey))
	// 	if err != nil {
	// 		panic("decrypt config db error. err = " + err.Error())
	// 	}
	// 	fmt.Println(string(configJson))
	// 	return
	// }

	// 初始化默认配置结构体
	dbConfig := DbConfig{
		ConfigOpt: ConfigOpt{DoEncrypt: false},
	}
	redisConfig := RedisConfig{
		ConfigOpt: ConfigOpt{DoEncrypt: false},
	}

	// 加载数据库配置文件
	if fileopt.FileExists(dbConfFile) {
		confByte, err := os.ReadFile(dbConfFile)
		if err != nil {
			panic("read db config file error. err = " + err.Error())
		}
		err = yaml.Unmarshal(confByte, &dbConfig)
		if err != nil {
			panic("unmarshal db config file error. err = " + err.Error())
		}
		dbConfig.DoEncrypt = true
	}
	// 加载 Redis 配置文件
	if fileopt.FileExists(redisConfFile) {
		confByte, err := os.ReadFile(redisConfFile)
		if err != nil {
			panic("read redis config file error. err = " + err.Error())
		}
		err = yaml.Unmarshal(confByte, &redisConfig)
		if err != nil {
			panic("unmarshal redis config file error. err = " + err.Error())
		}
		redisConfig.DoEncrypt = true
	}

	// 根据 update 参数判断是更新配置还是全量重建密钥及配置
	if *update { // 已存在rtk、rts、work.ey
		rootKey := hex.EncodeToString(utils.GetRootKey(utils.ReadRt(rootKeyFile, rootKeySaltFile)))
		workKeySecret, err := os.ReadFile(workKeyFile)
		if err != nil {
			panic("read config workkey error, check file permissions or use createRt. err = " + err.Error())
		}
		workKey, err := aes.AESGCMDecrypt(string(workKeySecret), rootKey)
		if err != nil {
			panic("decrypt config workkey error. err = " + err.Error())
		}
		createConfig(dbConfig, redisConfig, hex.EncodeToString(workKey), secretDbFile, secretRedisFile)
	} else { // 需要生成全部文件
		rootKey := hex.EncodeToString(utils.GetRootKey(utils.CreateRt(rootKeyFile, rootKeySaltFile)))
		createWorkKeyAndConfig(dbConfig, redisConfig, rootKey, workKeyFile, secretDbFile, secretRedisFile)
	}

	// 清理原始明文配置文件
	os.Remove(dbConfFile)
	os.Remove(redisConfFile)
}

func encryptAndWriteConfig(config interface{}, workKey, secretFile string) {
	configJsonByte, _ := json.Marshal(config)
	configJsonSecret, err := aes.AESGCMEncrypt(string(configJsonByte), workKey)
	if err != nil {
		panic("encrypt config error. err = " + err.Error())
	}
	file, err := os.OpenFile(secretFile, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0600)
	if err != nil {
		panic("write config_db file error." + err.Error())
	}
	defer file.Close()
	write := bufio.NewWriter(file)
	write.WriteString(configJsonSecret)
	write.Flush()
}

// createConfig 创建配置文件，根据是否需要加密将数据库和Redis配置信息加密后写入文件
// dbConfig: 数据库配置信息
// redisConfig: Redis配置信息
// workKey: 用于加密的工作密钥
// secretDbFile: 数据库配置加密后保存的文件路径
// secretRedisFile: Redis配置加密后保存的文件路径
func createConfig(dbConfig DbConfig, redisConfig RedisConfig, workKey, secretDbFile, secretRedisFile string) {
	// 将数据库配置信息加密后写入文件
	if dbConfig.DoEncrypt {
		encryptAndWriteConfig(dbConfig, workKey, secretDbFile)
	}
	// 将Redis配置信息加密后写入文件
	if redisConfig.DoEncrypt {
		encryptAndWriteConfig(redisConfig, workKey, secretRedisFile)
	}
}

// createWorkKeyAndConfig 生成工作密钥并创建配置文件
// 该函数会生成一个随机的工作密钥，使用根密钥对其进行加密，
// 将加密后的工作密钥保存到指定文件中，并基于工作密钥创建数据库和Redis的配置
// 参数:
//
//	dbConfig: 数据库配置信息
//	redisConfig: Redis配置信息
//	rootKey: 用于加密工作密钥的根密钥
//	workKeyFile: 存储加密后工作密钥的文件路径
//	secretDbFile: 存储数据库密钥配置的文件路径
//	secretRedisFile: 存储Redis密钥配置的文件路径
func createWorkKeyAndConfig(dbConfig DbConfig, redisConfig RedisConfig, rootKey string, workKeyFile, secretDbFile, secretRedisFile string) {
	// 生成工作密钥并用根密钥加密
	workKeyRandom := make([]byte, work_key_length)
	io.ReadFull(rand.Reader, workKeyRandom)
	workKey, err := aes.AESGCMEncrypt(string(workKeyRandom), rootKey)
	if err != nil {
		panic("encrypt workkey error. err = " + err.Error())
	}
	// 将加密后的工作密钥写入文件
	wkFile, err := os.OpenFile(workKeyFile, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0600)
	if err != nil {
		panic("write work.ey file error." + err.Error())
	}
	defer wkFile.Close()
	write := bufio.NewWriter(wkFile)
	write.WriteString(workKey)
	write.Flush()
	// 基于工作密钥创建数据库和Redis配置
	createConfig(dbConfig, redisConfig, hex.EncodeToString(workKeyRandom), secretDbFile, secretRedisFile)
}
