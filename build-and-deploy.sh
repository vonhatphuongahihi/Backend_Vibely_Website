#!/bin/bash

# Màu sắc cho output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Các biến môi trường mặc định
FRONTEND_URL="https://vibely-study-social-website.vercel.app"
BACKEND_URL="https://vibely-backend-h34f.onrender.com"

# Hỏi người dùng có muốn thay đổi URL không
read -p "Bạn có muốn thay đổi Frontend URL không? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    read -p "Nhập Frontend URL mới: " FRONTEND_URL
fi

read -p "Bạn có muốn thay đổi Backend URL không? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    read -p "Nhập Backend URL mới: " BACKEND_URL
fi

echo -e "${BLUE}=== Bắt đầu quy trình build và deploy Vibely Backend ===${NC}"
echo -e "${GREEN}Frontend URL: ${FRONTEND_URL}${NC}"
echo -e "${GREEN}Backend URL: ${BACKEND_URL}${NC}"

# Bước 1: Build project với Maven
echo -e "\n${GREEN}1. Building project với Maven...${NC}"
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Lỗi khi build project!"
    exit 1
fi

# Bước 2: Build Docker image với build arguments
echo -e "\n${GREEN}2. Building Docker image...${NC}"
docker build \
    --build-arg FRONTEND_URL=${FRONTEND_URL} \
    --build-arg BACKEND_URL=${BACKEND_URL} \
    -t vibely-backend .
if [ $? -ne 0 ]; then
    echo "Lỗi khi build Docker image!"
    exit 1
fi

# Bước 3: Tag image
echo -e "\n${GREEN}3. Tagging Docker image...${NC}"
docker tag vibely-backend vonhatphuongahihi/vibely-backend:latest
if [ $? -ne 0 ]; then
    echo "Lỗi khi tag Docker image!"
    exit 1
fi

# Bước 4: Push lên Docker Hub
echo -e "\n${GREEN}4. Pushing lên Docker Hub...${NC}"
docker push vonhatphuongahihi/vibely-backend:latest
if [ $? -ne 0 ]; then
    echo "Lỗi khi push lên Docker Hub!"
    exit 1
fi

# Bước 5: Chạy container locally (optional)
read -p "Bạn có muốn chạy container locally không? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo -e "\n${GREEN}5. Chạy container locally...${NC}"
    docker run -p 8081:8081 -d vibely-backend
fi

echo -e "\n${BLUE}=== Quy trình build và deploy hoàn tất ===${NC}" 