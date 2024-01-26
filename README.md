# accounting

## Running locally

### Build application

```
mvn package
```
### Build docker image

```
docker build -t docker.vzotov.ru/vzotov/accounting-application:dev .
```
### Run

You can use docker-compose to run this application locally.

```    
docker compose up -d --build --wait
```
Database connection URL: `jdbc:mysql://localhost:3306/accounting`
Application credentials
```
Username: accounting
Password: accounting
```

Root credentials
```
Username: root
Password: password
```
You can change these  settings in the `docker-compose.yml` file.

## Endpoints

### Health

https://localhost:8443/actuator/health

### Swagger UI

http://localhost:8080/swagger-ui.html
