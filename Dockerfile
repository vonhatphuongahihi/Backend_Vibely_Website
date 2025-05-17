# Sử dụng image Java chính thức
FROM eclipse-temurin:18-jdk-alpine

# Tạo thư mục app
WORKDIR /app

# Copy file jar đã build vào image
COPY target/*.jar app.jar

# Copy file .env
COPY .env .env

# Expose port 
EXPOSE 8081

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]