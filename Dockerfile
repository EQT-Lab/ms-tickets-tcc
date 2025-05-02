# Usando uma imagem base do JRE (Java 17)
FROM eclipse-temurin:17-jre-alpine
 
# Definir o diretório de trabalho
WORKDIR /app
 
# Copia todo o conteúdo da pasta target/quarkus-app
COPY target/quarkus-app /app/
 
# Define a variável de ambiente para o modo de develop
ENV QUARKUS_PROFILE=dev
 
# Expõe a porta padrão do Quarkus (8080)
EXPOSE 8080
 
# Comando para rodar o quarkus-run.jar
CMD ["java", "-jar", "/app/quarkus-run.jar"]