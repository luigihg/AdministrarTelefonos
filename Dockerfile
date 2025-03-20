# Usa una imagen base de Java
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos pom.xml y el wrapper de Maven
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Descarga las dependencias de Maven (si no existen)
RUN ./mvnw dependency:go-offline -B

# Copia el código fuente de la aplicación
COPY src ./src

# Empaqueta la aplicación usando Maven
RUN ./mvnw package -DskipTests

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["./mvnw", "spring-boot:run"]