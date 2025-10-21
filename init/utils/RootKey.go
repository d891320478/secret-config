package utils

import (
	"bufio"
	"crypto/sha256"
	"encoding/base64"
	"os"

	"github.com/aokoli/goutils"
	"golang.org/x/crypto/pbkdf2"
)

const root_key_factor = "FjhhKQdXYUMtBUtsX1VuUjkDZy5XCyRzDSIxaSx+dltgHmQIWF4KZVsgPlQ4Bw8ATSkOKC0ud2ECHUBDNigdJXAkBRgfAFMlbmYIaBZDTDhABRJOXmI/M2xUd2hjcmhURFxhXnxza2UpYh9aGyhmPksgVQRkKVsePk10VzA9cyY="

const root_key_length = 32

func CreateRt(root_key_file, root_key_salt_file string) (rtkStr, rtsStr string) {
	// 根密钥组件，key
	rtkStr, _ = goutils.CryptoRandom(128, 0, 127, false, false)
	rtkStr = base64.StdEncoding.EncodeToString([]byte(rtkStr))
	rtkFile, err := os.OpenFile(root_key_file, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0600)
	if err != nil {
		panic("write rtk file error." + err.Error())
	}
	defer rtkFile.Close()
	write := bufio.NewWriter(rtkFile)
	write.WriteString(rtkStr)
	write.Flush()
	// 根密钥组件，盐
	rtsStr, _ = goutils.CryptoRandom(128, 0, 127, false, false)
	rtsStr = base64.StdEncoding.EncodeToString([]byte(rtsStr))
	rtsFile, err := os.OpenFile(root_key_salt_file, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0600)
	if err != nil {
		panic("write rts file error." + err.Error())
	}
	defer rtsFile.Close()
	write = bufio.NewWriter(rtsFile)
	write.WriteString(rtsStr)
	write.Flush()

	return
}

func ReadRt(root_key_file, root_key_salt_file string) (rtkStr, rtsStr string) {
	keyFile, _ := os.Open(root_key_file)
	defer keyFile.Close()
	readKeyFile := bufio.NewReader(keyFile)
	rtkByte, _, err := readKeyFile.ReadLine()
	if err != nil {
		panic("read rtk file error." + err.Error())
	}
	saltFile, _ := os.Open(root_key_salt_file)
	defer saltFile.Close()
	readSaltFile := bufio.NewReader(saltFile)
	rtsByte, _, err := readSaltFile.ReadLine()
	if err != nil {
		panic("read rts file error." + err.Error())
	}
	return string(rtkByte), string(rtsByte)
}

func xor(k1 []byte, k2 []byte) []byte {
	l1 := len(k1)
	l2 := len(k2)
	l := l1
	if l < l2 {
		l = l2
	}
	rlt := make([]byte, l)
	for i := 0; i < l; i++ {
		var a byte = 0
		if i < l1 {
			a = k1[i]
		}
		var b byte = 0
		if i < l2 {
			b = k2[i]
		}
		rlt[i] = a ^ b
	}
	return rlt
}

func base64Decode(ori string) []byte {
	bt, err := base64.StdEncoding.DecodeString(ori)
	if err != nil {
		panic(ori + " base64 decode error." + err.Error())
	}
	return bt
}
func GetRootKey(rtk, rts string) (rootKeyByte []byte) {
	rootKeyByte = pbkdf2.Key(xor(base64Decode(rtk), base64Decode(root_key_factor)), base64Decode(rts), 100000, root_key_length, sha256.New)
	return
}
