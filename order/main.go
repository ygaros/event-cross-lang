package main

import (
	"order/server"
)

func main() {
	server.StartNewHttpServer(7001, "", "book.products.topic")
}
