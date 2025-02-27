FROM golang:1.21.5

WORKDIR /app

COPY . /app

RUN go build -o delivery

ENTRYPOINT ["./delivery"]