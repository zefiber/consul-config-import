echo '#####Starting the consul#####'
docker-compose.exe up -d consul

docker-compose.exe logs consul > consul.log
