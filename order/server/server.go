package server

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"

	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-kafka/v2/pkg/kafka"
	"github.com/ThreeDotsLabs/watermill/message"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/cors"
	"github.com/google/uuid"
)

type HttpServer interface {
	HandleOrder(w http.ResponseWriter, r *http.Request)
}
type httpServer struct {
	publisher    *kafka.Publisher
	bookingTopic string
}
type ProductRequest struct {
	Name     string `json:"name"`
	Price    int    `json:"price"`
	Quantity int    `json:"quantity"`
}
type AddressRequest struct {
	City    string `json:"city"`
	Country string `json:"country"`
	Street  string `json:"street"`
	ZipCode string `json:"zipCode"`
}
type CustomerRequest struct {
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
}
type OrderRequest struct {
	Id       uuid.UUID        `json:"id"`
	Address  AddressRequest   `json:"address"`
	Customer CustomerRequest  `json:"customer"`
	Products []ProductRequest `json:"products"`
}

func (s *httpServer) HandleOrder(w http.ResponseWriter, r *http.Request) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		log.Println(err)
		w.WriteHeader(http.StatusBadRequest)
	}
	defer r.Body.Close()
	request := OrderRequest{}
	if err := json.Unmarshal(body, &request); err != nil {
		log.Println("Failed to parse json data:", err)
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	newId, _ := uuid.NewRandom()
	request.Id = newId
	if marshaled, err := json.Marshal(request); err == nil {
		msg := message.NewMessage(watermill.NewUUID(), marshaled)

		if err := s.publisher.Publish(s.bookingTopic, msg); err != nil {
			panic(err)
		}

		if orderIdMarshaled, err := json.Marshal(newId); err == nil {
			w.Write(orderIdMarshaled)
			w.WriteHeader(http.StatusCreated)
		}
		return
	}
	log.Println("Failed to response to that request", request)
	w.WriteHeader(http.StatusBadRequest)

}
func prepareRouter() chi.Router {
	r := chi.NewRouter()
	r.Use(middleware.Logger)
	r.Use(cors.Handler(cors.Options{
		// AllowedOrigins:   []string{"https://foo.com"}, // Use this to allow specific origin hosts
		AllowedOrigins: []string{"https://*", "http://*"},
		// AllowOriginFunc:  func(r *http.Request, origin string) bool { return true },
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type", "X-CSRF-Token"},
		ExposedHeaders:   []string{"Link"},
		AllowCredentials: false,
		MaxAge:           300, // Maximum value not ignored by any of major browsers
	}))
	return r
}
func StartNewHttpServer(port int, kafkaUrl, bookingTopic string) {
	if kafkaUrl == "" {
		kafkaUrl = "kafka:9092"
	}
	publisher, err := kafka.NewPublisher(
		kafka.PublisherConfig{
			Brokers:   []string{kafkaUrl},
			Marshaler: kafka.DefaultMarshaler{},
		},
		watermill.NewStdLogger(false, false),
	)
	if err != nil {
		panic(err)
	}
	server := httpServer{
		publisher:    publisher,
		bookingTopic: bookingTopic,
	}
	r := prepareRouter()
	r.Post("/", server.HandleOrder)

	log.Fatalln(http.ListenAndServe(fmt.Sprintf(":%d", port), r))
}
