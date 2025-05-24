# [IE303.P22.Group3] - Vibely Educational Social Web

## Introduction
This project is part of the "Java Technology" course (IE303), aimed at developing a comprehensive educational social networking platform. Vibely combines social media features with educational tools to create an engaging learning environment for students. The platform enables students to interact, share knowledge, and support each other throughout their learning journey.

## Project Information
- **University**: University of Information Technology, VNU-HCM
- **Faculty**: School of Computer Science and Engineering
- **Course**: Java Technology (IE303.P22)
- **Instructor**: Nguyen Thanh Luan
- **Student Group**: Group 3

## Team Members
|  No.  |         Name         | Student ID |    Role     |
| :---: | :------------------: | :--------: | :---------: |
|   1   |    Vo Nhat Phuong    |  22521172  | Team Leader |
|   2   | Ngo Thi Nhu Quynh |  22521232  |   Member    |
|   3   | Do Luong Phuong Sang |  22521237  |   Member    |
|   4   |    Hoang Gia Minh    |  22520861  |   Member    |

## Project Overview
The Vibely educational social networking website is an online platform that combines social media features like Facebook (friending, messaging, and posting) with study support tools (viewing study materials, the Pomodoro technique, scheduling timetables, countdown timers for university entrance exams, and taking quizzes). This platform enables students to interact, share knowledge and resources, and support each other throughout their learning journey.


[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
* * * * *

Table of Contents
-----------------


- [\[IE303.P22.Group3\] - Vibely Educational Social Web](#ie303p22group3---vibely-educational-social-web)
  - [Introduction](#introduction)
  - [Project Information](#project-information)
  - [Team Members](#team-members)
  - [Project Overview](#project-overview)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
    - [**User Features**:](#user-features)
    - [**Admin Features**:](#admin-features)
  - [Tech Stack](#tech-stack)
  - [System Architecture](#system-architecture)
  - [Running the Project](#running-the-project)
    - [Step 1: Clone Both Projects](#step-1-clone-both-projects)
    - [Step 2: Configure Backend](#step-2-configure-backend)
    - [Step 3: Run Backend](#step-3-run-backend)
    - [Step 4: Run Frontend](#step-4-run-frontend)
    - [Accessing the Application](#accessing-the-application)


* * * * *
Features
--------

### **User Features**:
-   Sign up, log in, log out, reset password
-   Create, view and like stories
-   Create, view, edit, delete and interact with posts and videos (react, comment, share)
-   Send and accept friend requests
-   Search for other users
-   View, save and share study materials
-   Countdown timer for university entrance exams
-   View weather forecasts
-   Pomodoro mode for focused study sessions
-   Take quizzes to test knowledge
-   Plan study schedules
-   Chat with friends
-   View notifications
-   View other users' profiles
-   Manage account and profile settings
-   Help center and submit inquiries
-   Interact with AI Chatbot for study support
-   🌱 Upcoming Feature (in development):
Grow a Learning Tree - Complete your learning goals to grow your tree and unlock achievement badges
### **Admin Features**:
-   View statistical reports
-   Manage users
-   Manage posts
-   Manage study materials
-   Manage quizs
-   Manage inquiries
-   Change password
-   Admin account management
* * * * *

Tech Stack
----------

-   **Frontend**: HTML, Tailwind CSS, React.js, Next.js, Zustand
-   **Backend**: Java, Spring Boot 3.x, Spring Security, Spring Data MongoDB, JWT Authentication, OAuth2 (Google, Facebook, GitHub), Swagger (API Documentation), Maven
-   **Database**: MongoDB
-   **DevOps & Tools**: Docker, GitHub Actions (CI/CD), Cloudinary (Media Management)

* * * * *

System Architecture
----------

```mermaid
flowchart TD
    %% Frontend Layer
    subgraph "Frontend Layer"
        FE_USER["Frontend-User"]:::frontend
        FE_ADMIN["Frontend-Admin"]:::frontend
    end

    %% Backend Layer
    subgraph "Backend Layer"
        BE_API["Backend API"]:::backend
        API_DOC["API Documentation (Swagger)"]:::backend
    end

    %% Data & External Services
    subgraph "Data & External"
        DB["Database (MongoDB)"]:::database
        CLOUD["Cloudinary Integration"]:::external
    end

    %% Real-Time & AI Services
    subgraph "Real-Time & AI Services"
        SOCKET["Socket Server"]:::socket
        CHATBOT["Chatbot Service"]:::chatbot
        AI["External AI Providers"]:::external
    end

    %% CI/CD & Testing
    CI_CD["CI/CD & Testing"]:::ciCD

    %% Connections
    FE_USER -->|"calls"| BE_API
    FE_ADMIN -->|"calls"| BE_API
    BE_API -->|"docs-in"| API_DOC
    BE_API -->|"reads/writes"| DB
    BE_API -->|"media"| CLOUD
    BE_API <-->|"real-time"| SOCKET
    BE_API -->|"AIquery"| CHATBOT
    CHATBOT -->|"calls"| AI
    SOCKET -->|"updates"| FE_USER
    SOCKET -->|"updates"| FE_ADMIN
    CI_CD --- FE_USER
    CI_CD --- FE_ADMIN
    CI_CD --- BE_API
    CI_CD --- SOCKET
    CI_CD --- CHATBOT

    %% Click Events
    click FE_USER "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/frontend-user"
    click FE_ADMIN "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/frontend-admin"
    click BE_API "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/backend"
    click CHATBOT "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/chatbot-backend"
    click SOCKET "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/socket"
    click DB "https://github.com/vonhatphuongahihi/vibely-study-social-web/blob/main/backend/config/db.js"
    click CLOUD "https://github.com/vonhatphuongahihi/vibely-study-social-web/blob/main/backend/config/cloudinary.js"
    click API_DOC "https://github.com/vonhatphuongahihi/vibely-study-social-web/blob/main/backend/API/swagger.yaml"
    click CI_CD "https://github.com/vonhatphuongahihi/vibely-study-social-web/tree/main/tests"

    %% Styles
    classDef frontend fill:#ADD8E6,stroke:#000,stroke-width:2px;
    classDef backend fill:#90EE90,stroke:#000,stroke-width:2px;
    classDef chatbot fill:#FFDAB9,stroke:#000,stroke-width:2px;
    classDef socket fill:#E6E6FA,stroke:#000,stroke-width:2px;
    classDef database fill:#FFFACD,stroke:#000,stroke-width:2px;
    classDef external fill:#FFB6C1,stroke:#000,stroke-width:2px;
    classDef ciCD fill:#D3D3D3,stroke:#000,stroke-width:2px;

```
* * * * *


Running the Project
----------
### Step 1: Clone Both Projects

1. Clone Backend:
```bash
git clone https://github.com/vonhatphuongahihi/Backend_Vibely_Website
cd Backend_Vibely_Website
```

2. Clone Frontend:
```bash
git clone https://github.com/vonhatphuongahihi/Frontend_Vibely_Website
cd Frontend_Vibely_Website
```

### Step 2: Configure Backend

1. Create `application.properties` in `Backend_Vibely_Website/src/main/resources/`:
```properties
# Server Configuration
server.port=8081
spring.application.name=vibely-backend

# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://username:password@your-cluster.mongodb.net/your-database
spring.data.mongodb.database=your-database
spring.data.mongodb.auto-index-creation=true

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=7776000

# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your_google_client_id
spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8081/auth/google/callback
spring.security.oauth2.client.registration.google.scope=email,profile

# Facebook OAuth2 Configuration
spring.security.oauth2.client.registration.facebook.client-id=your_facebook_client_id
spring.security.oauth2.client.registration.facebook.client-secret=your_facebook_client_secret
spring.security.oauth2.client.registration.facebook.redirect-uri=http://localhost:8081/auth/facebook/callback
spring.security.oauth2.client.registration.facebook.scope=email,public_profile

# GitHub OAuth2 Configuration
spring.security.oauth2.client.registration.github.client-id=your_github_client_id
spring.security.oauth2.client.registration.github.client-secret=your_github_client_secret
spring.security.oauth2.client.registration.github.redirect-uri=http://localhost:8081/auth/github/callback
spring.security.oauth2.client.registration.github.scope=user:email,read:user

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Cloudinary Configuration
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

#ChatGPT Configuration
openai.api.key=your_api_key
openai.api.url=https://api.openai.com/v1/chat/completions

# Frontend URL
frontend.url=http://localhost:3000

# File Upload Configuration
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Step 3: Run Backend

1. Go to backend directory:
```bash
cd Backend_Vibely_Website
```

2. Run with Maven:
```bash
mvn spring-boot:run
```
Backend will run at: http://localhost:8081

### Step 4: Run Frontend

1. Go to frontend directory:
```bash
cd Frontend_Vibely_Website
```

2. Run frontend-user:
```bash
cd frontend-user
npm install
npm run dev
```
Frontend-user will run at: http://localhost:3000

3. Run frontend-admin:
```bash
cd frontend-admin
npm install
npm run dev
```
Frontend-admin will run at: http://localhost:3001

### Accessing the Application

- Backend API: http://localhost:8081
- Frontend User: http://localhost:3000
- Frontend Admin: http://localhost:3001
