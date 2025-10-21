package aes

import (
	"crypto/aes"
	"crypto/cipher"
	"encoding/hex"

	"github.com/aokoli/goutils"
)

const gcm_nonce_length = 16

// AESGCMEncrypt 使用AES-GCM模式加密数据
// 参数:
//
//	origin: 待加密的原始字符串
//	seed: 用于生成密钥的种子字符串，应为十六进制编码
//
// 返回值:
//
//	ciphertextStr: 加密后的密文字符串，包含加密数据和随机nonce
//	err: 加密过程中可能产生的错误
func AESGCMEncrypt(origin, seed string) (ciphertextStr string, err error) {
	// 生成随机nonce
	rr, err := goutils.CryptoRandom(gcm_nonce_length, 0, 127, false, false)
	if err != nil {
		return
	}
	nonceByte := []byte(rr)
	randNonce := hex.EncodeToString(nonceByte)
	// 解码种子并创建AES cipher
	seedByte, err := hex.DecodeString(seed)
	if err != nil {
		return
	}
	block, err := aes.NewCipher(seedByte)
	if err != nil {
		return
	}
	// 创建GCM模式加密器
	aesgcm, err := cipher.NewGCMWithNonceSize(block, gcm_nonce_length)
	if err != nil {
		return
	}
	// 执行加密并组合密文与nonce
	ciphertext := hex.EncodeToString(aesgcm.Seal(nil, nonceByte, []byte(origin), nil))

	return ciphertext + randNonce, err
}

// AESGCMDecrypt 使用AES-GCM模式解密十六进制编码的密文
// ciphertextStr: 十六进制编码的密文字符串，包含加密数据和nonce
// seed: 十六进制编码的密钥种子
// 返回解密后的原始字节数据和可能的错误信息
func AESGCMDecrypt(ciphertextStr, seed string) (originByte []byte, err error) {
	// 从输入字符串中分离密文和nonce
	ciphertext := ciphertextStr[:len(ciphertextStr)-gcm_nonce_length*2]
	nonce := ciphertextStr[len(ciphertextStr)-gcm_nonce_length*2:]
	// 解码十六进制密文
	ciphertextByte, err := hex.DecodeString(ciphertext)
	if err != nil {
		return
	}
	// 解码十六进制种子密钥
	seedByte, err := hex.DecodeString(seed)
	if err != nil {
		return
	}
	// 创建AES密码块
	block, err := aes.NewCipher(seedByte)
	if err != nil {
		return
	}
	// 创建GCM模式实例
	aesgcm, err := cipher.NewGCMWithNonceSize(block, gcm_nonce_length)
	if err != nil {
		return
	}
	// 解码nonce
	nonceByte, err := hex.DecodeString(nonce)
	if err != nil {
		return
	}
	// 执行解密操作
	originByte, err = aesgcm.Open(nil, nonceByte, ciphertextByte, nil)
	if err != nil {
		return
	}
	return
}
