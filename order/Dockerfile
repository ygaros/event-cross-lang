FROM golang:1.21.5

WORKDIR /app

COPY . /app

RUN go build -o order 

EXPOSE 7001

ENTRYPOINT ["./order"]