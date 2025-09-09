#FROM eclipse-temurin:21-jdk-alpine
#VOLUME /tmp
#COPY *.jar Authentication.jar
#COPY --chown=appuser:appgroup ../applications/app-service/build/libs/Authentication.jar app.jar
#ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
## Replace with a non-root user to avoid running the container with excessive privileges
#USER appuser
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar Authentication.jar" ]
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp

# Crear usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el jar desde build/libs
COPY --chown=appuser:appgroup ../applications/app-service/build/libs/Authentication.jar app.jar

# Config JVM (opciones válidas para Temurin/HotSpot)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]