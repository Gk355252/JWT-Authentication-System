# SecureProfile: JWT Authentication System

![JWT Logo](https://jwt.io/img/logo-asset.svg)

## 🚀 Project Overview

SecureProfile is a robust Spring Boot application implementing a secure user authentication and profile management system using JWT (JSON Web Tokens). This project provides a comprehensive solution for modern web applications requiring secure user management.

## 🔑 Key Features

- 📧 User registration with email verification
- 🔒 JWT-based authentication
- 🔢 Two-factor authentication (2FA) using email OTP
- 🔐 Secure password management (reset, change)
- 👤 Comprehensive user profile management
- ⚡ Asynchronous data fetching for improved performance
- 📚 Swagger UI for API documentation and testing

## 🛠 Technology Stack

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

## 📋 Prerequisites

- JDK 11 or later
- Maven 3.6+
- Your preferred IDE (IntelliJ IDEA, Eclipse, etc.)

## 🚀 Getting Started

1. Clone the repository: git clone https://github.com/Gk355252/JWT-Authentication-System.git
2. Navigate to the project directory: cd JWT-Authentication-System
3. Build the project: mvn clean install
4. Run the application: mvn spring-boot:run

The application will start running at `http://localhost:8080`.

## 📚 API Documentation

Swagger UI is integrated for easy API testing and documentation. Once the application is running, access the Swagger UI at: http://localhost:8080/swagger-ui/index.html
![Swagger UI](https://github.com/Gk355252/JWT-Authentication-System/blob/main/swagger-ui-screenshot.png?raw=true)

## 🔒 Security Measures

- JWT for stateless authentication
- Password encryption using BCrypt
- Email verification for new accounts
- Two-factor authentication with OTP
- Secure password reset mechanism

## 🧪 Testing

To run the tests, execute: mvn test
