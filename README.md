# Indica Clinic API

Hệ thống quản lý phòng khám với Spring Boot, JWT Authentication, và PostgreSQL.

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Cấu hình](#cấu-hình)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Database Schema](#database-schema)
- [Roles và Permissions](#roles-và-permissions)

## 🎯 Tổng quan

Dự án Indica Clinic là hệ thống quản lý phòng khám được xây dựng bằng Spring Boot, cung cấp:
- Xác thực người dùng với JWT và Refresh Token
- Quản lý người dùng và phân quyền
- RESTful API với Swagger documentation
- Kết nối PostgreSQL database

## 🛠 Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - Xác thực và phân quyền
- **Spring Data JPA** - ORM và database access
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **Swagger/OpenAPI 3** - API documentation
- **Lombok** - Giảm boilerplate code
- **Maven** - Dependency management

## 📁 Cấu trúc dự án

```
indica/
├── src/
│   ├── main/
│   │   ├── java/com/clinic/indica/
│   │   │   ├── config/              # Cấu hình
│   │   │   │   ├── DataInitializer.java      # Khởi tạo dữ liệu mặc định
│   │   │   │   ├── SecurityConfig.java      # Cấu hình Spring Security
│   │   │   │   └── SwaggerConfig.java        # Cấu hình Swagger/OpenAPI
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   └── AuthController.java      # Controller xử lý authentication
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── AuthResponse.java        # Response sau khi login/refresh
│   │   │   │   ├── LoginRequest.java        # Request đăng nhập
│   │   │   │   └── RefreshTokenRequest.java # Request refresh token
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java                # Entity người dùng
│   │   │   │   └── Role.java                # Entity vai trò
│   │   │   ├── exception/           # Exception handlers
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidTokenException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/         # JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── RoleRepository.java
│   │   │   ├── security/            # Security components
│   │   │   │   └── JwtAuthenticationFilter.java # JWT filter
│   │   │   ├── service/             # Service layer
│   │   │   │   ├── impl/                    # Service implementations
│   │   │   │   │   ├── AuthServiceImpl.java
│   │   │   │   │   ├── RoleServiceImpl.java
│   │   │   │   │   └── UserServiceImpl.java
│   │   │   │   ├── AuthService.java         # Interface authentication
│   │   │   │   ├── CustomUserDetailsService.java # UserDetailsService
│   │   │   │   ├── JwtService.java          # JWT utilities
│   │   │   │   ├── RoleService.java         # Interface quản lý roles
│   │   │   │   └── UserService.java         # Interface quản lý users
│   │   │   └── IndicaApplication.java       # Main application class
│   │   └── resources/
│   │       └── application.properties      # Application configuration
│   └── test/                         # Test files
├── pom.xml                          # Maven dependencies
└── README.md                        # File này
```

## 🚀 Cài đặt và chạy

### Yêu cầu

- Java 17 hoặc cao hơn
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Các bước cài đặt

1. **Clone repository** (nếu có)
   ```bash
   git clone <repository-url>
   cd indica
   ```

2. **Cấu hình database**
   - Tạo database PostgreSQL: `indica_clinic`
   - Cập nhật thông tin kết nối trong `application.properties`

3. **Build project**
   ```bash
   mvn clean install
   ```

4. **Chạy ứng dụng**
   ```bash
   mvn spring-boot:run
   ```
   Hoặc chạy class `IndicaApplication` từ IDE

5. **Truy cập Swagger UI**
   - URL: `http://localhost:8080/swagger-ui.html`
   - API Docs: `http://localhost:8080/api-docs`

## ⚙️ Cấu hình

### Database Configuration

File `application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/indica_clinic
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### JWT Configuration

```properties
jwt.secret=your-secret-key-here
jwt.access-token-expiration=86400000    # 24 hours (milliseconds)
jwt.refresh-token-expiration=604800000 # 7 days (milliseconds)
```

### Swagger Configuration

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

## 📡 API Endpoints

### Authentication

#### 1. Đăng nhập
```
POST /api/auth/login
Content-Type: application/json

Request Body:
{
  "username": "admin",
  "password": "admin"
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@indica.clinic",
  "fullName": "Super Administrator",
  "roles": ["SUPERADMIN"]
}
```

#### 2. Refresh Token
```
POST /api/auth/refresh
Content-Type: application/json

Request Body:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Response: (Tương tự như login response)
```

### Sử dụng Access Token

Để gọi các API được bảo vệ, thêm header:
```
Authorization: Bearer <access_token>
```

## 🔐 Authentication

### JWT Token Flow

1. **Login**: User gửi username/password → Nhận access token và refresh token
2. **Access Token**: Dùng để xác thực các request (thời hạn ngắn)
3. **Refresh Token**: Dùng để lấy access token mới khi hết hạn (thời hạn dài)
4. **Token Storage**: Refresh token được lưu trong database

### Security Configuration

- **Public Endpoints**: 
  - `/api/auth/**` - Authentication endpoints
  - `/api-docs/**`, `/swagger-ui/**` - Swagger documentation
  - `/v3/api-docs/**` - OpenAPI docs

- **Protected Endpoints**: Tất cả các endpoint khác yêu cầu JWT token

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT true,
    refresh_token VARCHAR(255),
    refresh_token_expiry TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Roles Table
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);
```

### User Roles (Many-to-Many)
```sql
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

## 👥 Roles và Permissions

Hệ thống hỗ trợ các roles sau:

1. **SUPERADMIN** - Quản trị viên cấp cao
   - Toàn quyền truy cập hệ thống
   - Quản lý tất cả users và roles

2. **ADMIN** - Quản trị viên
   - Quản lý phòng khám
   - Quản lý nhân viên

3. **RECEPTIONIST** - Lễ tân
   - Đăng ký bệnh nhân
   - Đặt lịch hẹn

4. **DOCTOR** - Bác sĩ
   - Xem và quản lý bệnh án
   - Kê đơn thuốc

5. **NURSE** - Y tá
   - Hỗ trợ bác sĩ
   - Chăm sóc bệnh nhân

6. **PATIENT** - Bệnh nhân
   - Xem thông tin cá nhân
   - Xem lịch hẹn

## 🔑 Tài khoản mặc định

Khi ứng dụng khởi động lần đầu, hệ thống tự động tạo:

- **Username**: `admin`
- **Password**: `admin`
- **Role**: `SUPERADMIN`
- **Email**: `admin@indica.clinic`

⚠️ **Lưu ý**: Đổi mật khẩu ngay sau lần đăng nhập đầu tiên trong môi trường production!

## 🧪 Testing

### Test với Swagger UI

1. Truy cập `http://localhost:8080/swagger-ui.html`
2. Test endpoint `/api/auth/login` với:
   ```json
   {
     "username": "admin",
     "password": "admin"
   }
   ```
3. Copy `accessToken` từ response
4. Click nút "Authorize" ở đầu trang Swagger
5. Nhập: `Bearer <accessToken>`
6. Test các API được bảo vệ

### Test với cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Sử dụng token
curl -X GET http://localhost:8080/api/your-endpoint \
  -H "Authorization: Bearer <access_token>"
```

## 📝 Service Layer

### UserService

Quản lý người dùng:
- `createUser()` - Tạo user mới
- `updateUser()` - Cập nhật thông tin user
- `deleteUser()` - Xóa user
- `getUserById()`, `getUserByUsername()`, `getUserByEmail()` - Tìm user
- `addRoleToUser()`, `removeRoleFromUser()`, `updateUserRoles()` - Quản lý roles
- `updateRefreshToken()`, `clearRefreshToken()` - Quản lý refresh token

### RoleService

Quản lý vai trò:
- `createRole()` - Tạo role mới
- `updateRole()` - Cập nhật role
- `deleteRole()` - Xóa role
- `getRoleById()`, `getRoleByName()` - Tìm role
- `getAllRoles()` - Lấy tất cả roles
- `initializeRoles()` - Khởi tạo roles mặc định

### AuthService

Xác thực:
- `login()` - Đăng nhập và tạo tokens
- `refreshToken()` - Làm mới access token

## 🐛 Exception Handling

Hệ thống có `GlobalExceptionHandler` để xử lý:

- `ResourceNotFoundException` - 404 Not Found
- `DuplicateResourceException` - 409 Conflict
- `InvalidTokenException` - 401 Unauthorized
- `MethodArgumentNotValidException` - 400 Bad Request (Validation errors)

## 🔧 Development

### Thêm Controller mới

1. Tạo class trong package `controller`
2. Đánh dấu `@RestController` và `@RequestMapping`
3. Inject service interfaces
4. Thêm Swagger annotations nếu cần

### Thêm Service mới

1. Tạo interface trong package `service`
2. Tạo implementation trong package `service/impl`
3. Đánh dấu implementation với `@Service`
4. Inject repositories và các services khác

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [Swagger/OpenAPI](https://swagger.io/)

## 📄 License

Copyright © 2024 Indica Clinic

---

**Lưu ý**: Đây là tài liệu cấu trúc dự án. Để biết thêm chi tiết về implementation, xem source code.

