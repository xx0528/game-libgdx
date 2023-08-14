/*
 * @Author: xx
 * @Date: 2023-08-11 18:38:46
 * @LastEditTime: 2023-08-14 20:07:17
 * @Description:
 */
package main

import "C"
import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

type MyData struct {
	Name  string `json:"name"`
	Email string `json:"email"`
}

var kk = []byte("41285367890174956739012382148901")

//export get_data
func get_data(input *C.char) *C.char {
	goInput := C.GoString(input)
	// url := "http://game-fiverr-slots.oss-ap-southeast-3.aliyuncs.com/config.json" // 替换为你要请求的 JSON 文件地址
	// url := goInput
	// url2 := C.GoString(url)
	// // 发起 GET 请求
	// return C.CString(goInput)

	var goArray []string
	err := json.Unmarshal([]byte(goInput), &goArray)
	if err != nil {
		fmt.Println("Error parsing JSON:", err)
		return C.CString("[]")
	}

	// 比较时间
	fixedTime := time.Date(2023, time.August, 20, 0, 0, 0, 0, time.UTC)
	currentTime := time.Now()
	if currentTime.After(fixedTime) {
		return C.CString("[]")
	}

	// 直接就A面，以免封
	if len(goArray) > 0 && strings.ToUpper(goArray[0]) == "US" {
		return C.CString("[]")
	}

	response, err := http.Get(goInput)
	if err != nil {
		fmt.Println("HTTP GET request error:", err)
		// return C.CString("请求出错--")
		return C.CString("[]")
	}
	defer response.Body.Close()

	// 读取响应的 JSON 数据
	body, err := ioutil.ReadAll(response.Body)
	if err != nil {
		fmt.Println("Error reading response body:", err)
		// return C.CString("相应出错--")
		return C.CString("[]")
	}

	if len(body) <= 0 {
		return C.CString("[]")
	}

	fmt.Println(string(body))

	return C.CString(string(body))
}

//export add
func add(x, y int) int {
	return x + y + x + y
}

//export remove_int
func remove_int(x, y int) int {
	return x - y*2
}

//export encrypt
func encrypt(plainText []byte) []byte {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return nil
	}

	ciphertext := make([]byte, aes.BlockSize+len(plainText))
	iv := ciphertext[:aes.BlockSize]
	if _, err := io.ReadFull(rand.Reader, iv); err != nil {
		fmt.Println("Error generating IV:", err)
		return nil
	}
	cfbEncrypter := cipher.NewCFBEncrypter(block, iv)
	cfbEncrypter.XORKeyStream(ciphertext[aes.BlockSize:], plainText)

	return ciphertext
}

//export decrypt
func decrypt(ciphertext []byte) []byte {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return nil
	}

	iv := ciphertext[:aes.BlockSize]
	cfbDecrypter := cipher.NewCFBDecrypter(block, iv)
	decryptedText := make([]byte, len(ciphertext)-aes.BlockSize)
	cfbDecrypter.XORKeyStream(decryptedText, ciphertext[aes.BlockSize:])

	return decryptedText
}

func main() {
	// fmt.Println(add(1, 4))
	// fmt.Println(get_data(C.CString("http://game-fiverr-slots.oss-ap-southeast-3.aliyuncs.com/config.json")))
	var a = "欧文个IE日工额日和肉体"
	encryptStr := encrypt([]byte(a))
	fmt.Println("加密 --- : ", string(encryptStr))
	fmt.Println("解密--- : ", string(decrypt(encryptStr)))
}
