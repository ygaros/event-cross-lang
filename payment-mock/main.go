package main

import "payment-mock/server"

func main() {
	server.StartNewHttpServer(7002, "", "book.products.payment.topic")
}
