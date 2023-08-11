/*
 * @Author: xx
 * @Date: 2023-08-11 18:38:46
 * @LastEditTime: 2023-08-11 19:30:05
 * @Description:
 */
package main

import "C"
import "fmt"

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
}
