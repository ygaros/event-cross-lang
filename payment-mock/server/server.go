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
	HandlePayment(w http.ResponseWriter, r *http.Request)
}
type httpServer struct {
	publisher          *kafka.Publisher
	paymentStatusTopic string
}

const (
	INITIALIZED = "INITIALIZED"
	IN_PROGRESS = "IN_PROGRESS"
	FINISHED    = "FINISHED"
)

type PaymentRequest struct {
	OrderId uuid.UUID `json:"orderId"`
	Status  string    `json:"status"`
	Type    string    `json:"type"`
}

func (s *httpServer) HandlePayment(w http.ResponseWriter, r *http.Request) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		log.Println(err)
		w.WriteHeader(http.StatusBadRequest)
	}
	defer r.Body.Close()
	request := PaymentRequest{}
	if err := json.Unmarshal(body, &request); err != nil {
		log.Println("Failed to parse json data:", err)
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	msg := message.NewMessage(watermill.NewUUID(), body)

	if err := s.publisher.Publish(s.paymentStatusTopic, msg); err != nil {
		panic(err)
	}
	w.WriteHeader(http.StatusCreated)
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
func StartNewHttpServer(port int, kafkaUrl, paymentStatusTopic string) {
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
		publisher:          publisher,
		paymentStatusTopic: paymentStatusTopic,
	}
	r := prepareRouter()
	r.Post("/", server.HandlePayment)

	log.Fatalln(http.ListenAndServe(fmt.Sprintf(":%d", port), r))
}
