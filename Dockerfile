FROM openjdk:10-jdk

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

ADD /target/*.jar /app.jar

CMD echo "The application will starting now..." && \
    sleep 0 && \
    java ${JAVA_OPTS} \
      -Djava.security.egd=file:/dev/./urandom -jar /app.jar