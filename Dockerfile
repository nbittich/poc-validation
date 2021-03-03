FROM maven:3-openjdk-11
COPY . .
RUN mvn clean install -DskipTests
ENTRYPOINT ["java","-jar","/target/shacl-validation.jar"]
