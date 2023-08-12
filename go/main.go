/*
 * @Author: xx
 * @Date: 2023-08-11 18:38:46
 * @LastEditTime: 2023-08-12 18:10:53
 * @Description:
 */
package main

import "C"
import (
	"fmt"
	"io/ioutil"
	"net/http"
)

type MyData struct {
	Name  string `json:"name"`
	Email string `json:"email"`
}

//export get_data
func get_data(input *C.char) *C.char {
	goInput := C.GoString(input)
	// url := "http://game-fiverr-slots.oss-ap-southeast-3.aliyuncs.com/config.json" // 替换为你要请求的 JSON 文件地址
	// url := goInput
	// url2 := C.GoString(url)
	// // 发起 GET 请求
	// return C.CString(goInput)
	response, err := http.Get(goInput)
	if err != nil {
		fmt.Println("HTTP GET request error:", err)
		return C.CString("请求出错--")
	}
	defer response.Body.Close()

	// 读取响应的 JSON 数据
	body, err := ioutil.ReadAll(response.Body)
	if err != nil {
		fmt.Println("Error reading response body:", err)
		return C.CString("相应出错--")
	}

	// // 解析 JSON 数据
	// var data MyData
	// err = json.Unmarshal(body, &data)
	// if err != nil {
	// 	fmt.Println("JSON unmarshal error:", err)
	// 	return C.CString("解析出错--")
	// }

	// // 输出解析后的数据
	// fmt.Println("Name:", data.Name)
	// fmt.Println("Email:", data.Email)
	fmt.Println(string(body))
	return C.CString(string(body))
}

//export add
func add(x, y int) int {
	fmt.Println("走了这里吗？")
	return x + y + x + y
}

//export remove_int
func remove_int(x, y int) int {
	fmt.Println("goiergjorie")
	return x - y*2
}

func main() {
	fmt.Println(add(1, 4))
	fmt.Println(get_data(C.CString("http://game-fiverr-slots.oss-ap-southeast-3.aliyuncs.com/config.json")))
}
