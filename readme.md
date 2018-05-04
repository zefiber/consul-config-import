# The harmony-core-config-seed is the spring boot project to import all microservice properties.


# Sample project included
- app - spring boot project

## Development Stack
- Java
- Maven
- Spring Boot
- Consul

## Run
### Run for local dev environment 
- Run start-consul.sh to start up the consul service.
- Run import-properties.sh to import all properties under ServiceConfigurations folder
- Access consul configuration web via http://192.168.99.100:8500

 
## Requirements
- Windows 10/Windows 7
- Docker Tool box
- Spring Cloud Consul
 
## Docker Install
- [Windows 10](https://docs.docker.com/docker-for-windows/install/)
- [Windows 7 Legacy](https://docs.docker.com/toolbox/toolbox_install_windows/)
