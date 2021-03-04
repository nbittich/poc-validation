FROM maven:3-openjdk-11
COPY . .
RUN mvn clean install -DskipTests
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /target/shacl-validation.jar"]
