package main

import "C"

//export add
func add(x, y int) int {
	// fmt.Println("走了这里吗？")
	return x + y + x + y
}

//export remove_int
func remove_int(x, y int) int {
	// fmt.Println("goiergjorie")
	return x - y*2
}

func main() {
}
