# accounting

## Running locally

### Generate dhparam

    cd ./nginx/dhparam
    sudo openssl dhparam -out dhparam-2048.pem 2048

### Generate SSL certificates

    cd ./nginx/certs
    sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx-selfsigned.key -out nginx-selfsigned.crt

### Build application

    mvn package

### Build docker image

    docker build -t docker.vzotov.ru/vzotov/accounting-application:dev .

### Run

You can use docker-compose to run this application locally.

    docker compose up -d

### Cleanup

    docker-compose down
    rm -r -fo mysql 
    docker rm -f $(docker ps -a -q)
    docker volume rm $(docker volume ls -q)

## Endpoints

### Health

https://localhost:8443/actuator/health

### Swagger UI

http://localhost:8080/swagger-ui.html

## Development workflow

#### Replace SNAPSHOT versions in pom.xml

#### Create release tag 

    mvn gitflow:release

#### Create GitHub release

    gh release create 1.1 -t v1.1 --generate-notes

#### Wait until the release is published

    gh run watch

