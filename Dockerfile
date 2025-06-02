# Sử dụng image Java chính thức
FROM eclipse-temurin:18-jdk-alpine

# Tạo thư mục app
WORKDIR /app

# Copy file jar đã build vào image
COPY target/*.jar app.jar

# Copy file application.properties
COPY src/main/resources/application.properties /app/application.properties

# Expose port 
EXPOSE 8081

# Build arguments cho các biến môi trường
ARG FRONTEND_URL=https://vibely-study-social-website.vercel.app
ARG BACKEND_URL=https://vibely-backend-h34f.onrender.com

# Biến môi trường mặc định
ENV FRONTEND_URL=${FRONTEND_URL}
ENV BACKEND_URL=${BACKEND_URL}

# Lệnh chạy ứng dụng với biến môi trường
ENTRYPOINT ["sh", "-c", "java -jar app.jar --frontend.url=${FRONTEND_URL} --backend.url=${BACKEND_URL}"]