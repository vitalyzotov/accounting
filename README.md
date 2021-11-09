# accounting

## Running locally

You can use docker-compose to run this application locally.

    docker-compose up -d

Cleanup

    docker-compose down
    rm -r -fo mysql 
    docker rm -f $(docker ps -a -q)
    docker volume rm $(docker volume ls -q)

