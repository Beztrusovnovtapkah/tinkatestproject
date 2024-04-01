# Используем образ с Java и устанавливаем OpenJDK
FROM openjdk:17-jre-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/testtinka.jar app.jar

# Возможно, вам также потребуется скопировать файл конфигурации базы данных
# COPY src/main/resources/application.properties application.properties

# Открываем порт, на котором будет работать ваше приложение
EXPOSE 8080

# Команда для запуска приложения
CMD ["java", "-jar", "app.jar"]
