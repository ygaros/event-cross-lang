FROM golang:1.21.5

WORKDIR /app

COPY . /app

RUN go build -o payment-mock 

EXPOSE 7002

ENTRYPOINT ["./payment-mock"]