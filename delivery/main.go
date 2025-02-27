package main

import (
	"context"
	"log"

	"github.com/Shopify/sarama"
	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-kafka/v2/pkg/kafka"
	"github.com/ThreeDotsLabs/watermill/message"
)

const (
	kafkaUrl string = "kafka:9092"
	topic    string = "deliver.topic"
)

func main() {

	saramaSubscriberConfig := kafka.DefaultSaramaSubscriberConfig()
	// equivalent of auto.offset.reset: earliest
	saramaSubscriberConfig.Consumer.Offsets.Initial = sarama.OffsetOldest

	subscriber, err := kafka.NewSubscriber(
		kafka.SubscriberConfig{
			Brokers:               []string{kafkaUrl},
			Unmarshaler:           kafka.DefaultMarshaler{},
			OverwriteSaramaConfig: saramaSubscriberConfig,
			ConsumerGroup:         "delivery_consumer_group",
		},
		watermill.NewStdLogger(false, false),
	)
	if err != nil {
		panic(err)
	}

	// publisher, err = kafka.NewPublisher(
	// 	kafka.PublisherConfig{
	// 		Brokers:   []string{kafkaUrl},
	// 		Marshaler: kafka.DefaultMarshaler{},
	// 	},
	// 	watermill.NewStdLogger(false, false),
	// )
	// if err != nil {
	// 	panic(err)
	// }

	messages, err := subscriber.Subscribe(context.Background(), topic)
	if err != nil {
		panic(err)
	}
	process(messages)
}
func process(messages <-chan *message.Message) {
	for msg := range messages {
		log.Printf("received payment finished event: %s, payload: %s", msg.UUID, string(msg.Payload))

		// we need to Acknowledge that we received and processed the message,
		// otherwise, it will be resent over and over again.
		msg.Ack()
	}
}
