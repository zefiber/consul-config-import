FROM java:8
ENV JVM_ARGUMENTS ''

# environment variables
ENV APP_DIR=/harmony-core-config-seed
ADD ../ServiceConfigurations $APP_DIR/ServiceConfigurations
#set the working directory
WORKDIR $APP_DIR

ARG JAR_FILE
ADD ${JAR_FILE} harmony-core-config-seed.jar
ADD wrapper.sh wrapper.sh
RUN bash -c 'chmod +x /wrapper.sh'
RUN bash -c 'touch /harmony-core-config-seed.jar'
ENTRYPOINT ["/bin/bash", "/wrapper.sh"]
