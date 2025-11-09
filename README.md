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
- [Response Format](#response-format)
- [Tính năng bảo mật](#tính-năng-bảo-mật)

## 🎯 Tổng quan

Dự án Indica Clinic là hệ thống quản lý phòng khám được xây dựng bằng Spring Boot, cung cấp:
- Xác thực người dùng với JWT và Refresh Token
- Hỗ trợ đăng nhập trên nhiều thiết bị
- Quản lý người dùng và phân quyền (SUPERADMIN, ADMIN, RECEPTIONIST, DOCTOR, NURSE, PATIENT)
- API quản lý profile cá nhân
- RESTful API với Swagger documentation
- Response format chuẩn (ApiResponse)
- Kết nối PostgreSQL database

## 🛠 Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Security** - Xác thực và phân quyền
- **Spring Data JPA** - ORM và database access
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **Swagger/OpenAPI 2.6.0** - API documentation
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
│   │   │   │   ├── AuthController.java      # Controller xử lý authentication
│   │   │   │   ├── ProfileController.java   # Controller quản lý profile cá nhân
│   │   │   │   └── UserController.java      # Controller quản lý users (Admin)
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── ApiResponse.java         # Format response chuẩn
│   │   │   │   ├── AuthResponse.java        # Response sau khi login/refresh
│   │   │   │   ├── CreateUserRequest.java   # Request tạo user
│   │   │   │   ├── LoginRequest.java        # Request đăng nhập
│   │   │   │   ├── RefreshTokenRequest.java # Request refresh token
│   │   │   │   ├── UpdateProfileRequest.java # Request cập nhật profile
│   │   │   │   ├── UpdateUserRequest.java   # Request cập nhật user
│   │   │   │   └── UserResponse.java        # Response thông tin user
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── RefreshToken.java        # Entity refresh token (nhiều thiết bị)
│   │   │   │   ├── Role.java                # Entity vai trò
│   │   │   │   └── User.java                # Entity người dùng
│   │   │   ├── exception/           # Exception handlers
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidTokenException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/         # JPA Repositories
│   │   │   │   ├── RefreshTokenRepository.java # Repository refresh tokens
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── UserRepository.java
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
  "password": "admin",
  "deviceId": "device-123",      // Optional
  "deviceName": "iPhone 14"       // Optional
}

Response Format (ApiResponse):
{
  "code": 200,
  "message": "Đăng nhập thành công",
  "status": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@indica.clinic",
    "fullName": "Super Administrator",
    "roles": ["SUPERADMIN"]
  }
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

Response Format (ApiResponse):
{
  "code": 200,
  "message": "Làm mới token thành công",
  "status": "SUCCESS",
  "data": { ... } // Tương tự như login response
}
```

### Profile Management (Yêu cầu JWT Token)

#### 1. Lấy thông tin cá nhân
```
GET /api/profile
Authorization: Bearer <access_token>

Response:
{
  "code": 200,
  "message": "Lấy thông tin cá nhân thành công",
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@indica.clinic",
    "fullName": "Super Administrator",
    "phoneNumber": "0123456789",
    "enabled": true,
    "roles": ["SUPERADMIN"],
    "createdAt": "2024-11-09T20:00:00",
    "updatedAt": "2024-11-09T20:00:00"
  }
}
```

#### 2. Cập nhật thông tin cá nhân
```
PUT /api/profile
Authorization: Bearer <access_token>
Content-Type: application/json

Request Body:
{
  "fullName": "Nguyễn Văn A",        // Optional
  "email": "newemail@example.com",    // Optional
  "phoneNumber": "0987654321",         // Optional
  "password": "newpassword123"        // Optional - Khi đổi sẽ logout tất cả thiết bị
}

Response (khi đổi mật khẩu):
{
  "code": 200,
  "message": "Cập nhật thông tin thành công. Mật khẩu đã được đổi, tất cả các thiết bị đã bị đăng xuất.",
  "status": "SUCCESS",
  "data": { ... }
}
```

### User Management (Chỉ SUPERADMIN và ADMIN)

#### 1. Tạo người dùng mới
```
POST /api/users
Authorization: Bearer <access_token>
Content-Type: application/json

Request Body:
{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "fullName": "New User",
  "phoneNumber": "0123456789",
  "enabled": true,
  "roles": ["PATIENT"]
}

Response Format (ApiResponse):
{
  "code": 201,
  "message": "Tạo người dùng thành công",
  "status": "CREATED",
  "data": { ... }
}
```

#### 2. Lấy danh sách người dùng
```
GET /api/users
Authorization: Bearer <access_token>
```

#### 3. Lấy thông tin người dùng theo ID
```
GET /api/users/{id}
Authorization: Bearer <access_token>
```

#### 4. Cập nhật người dùng
```
PUT /api/users/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

Request Body:
{
  "username": "updateduser",     // Optional
  "password": "newpass",          // Optional
  "email": "newemail@example.com", // Optional
  "fullName": "Updated Name",     // Optional
  "phoneNumber": "0987654321",     // Optional
  "enabled": true,                // Optional
  "roles": ["DOCTOR"]             // Optional
}
```

#### 5. Xóa người dùng
```
DELETE /api/users/{id}
Authorization: Bearer <access_token>
```

### Sử dụng Access Token

Để gọi các API được bảo vệ, thêm header:
```
Authorization: Bearer <access_token>
```

## 🔐 Authentication

### JWT Token Flow

1. **Login**: User gửi username/password → Nhận access token và refresh token
2. **Access Token**: Dùng để xác thực các request (thời hạn ngắn - 24 giờ)
3. **Refresh Token**: Dùng để lấy access token mới khi hết hạn (thời hạn dài - 7 ngày)
4. **Token Storage**: Refresh token được lưu trong bảng `refresh_tokens` riêng (hỗ trợ nhiều thiết bị)

### Đăng nhập nhiều thiết bị

- Mỗi thiết bị có refresh token riêng
- User có thể đăng nhập trên nhiều thiết bị cùng lúc
- Mỗi refresh token lưu thông tin: deviceId, deviceName, ipAddress, userAgent
- Khi đổi mật khẩu → Tất cả refresh tokens bị xóa → Logout tất cả thiết bị

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
    phone_number VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Refresh Tokens Table
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(255),
    device_name VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    last_used_at TIMESTAMP
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
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
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
- **Full Name**: `Super Administrator`

⚠️ **Lưu ý**: Đổi mật khẩu ngay sau lần đăng nhập đầu tiên trong môi trường production!

## 🔒 Tính năng bảo mật

### Đăng nhập nhiều thiết bị
- User có thể đăng nhập trên nhiều thiết bị cùng lúc
- Mỗi thiết bị có refresh token riêng
- Refresh token lưu thông tin thiết bị: deviceId, deviceName, IP, User-Agent

### Đổi mật khẩu
- Khi user đổi mật khẩu → Tất cả refresh tokens bị xóa
- Tất cả thiết bị sẽ bị logout tự động
- User phải đăng nhập lại với mật khẩu mới

### Phân quyền
- **SUPERADMIN, ADMIN**: Có quyền tạo và quản lý tài khoản user
- **Tất cả user đã đăng nhập**: Có quyền cập nhật thông tin cá nhân của mình

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
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","deviceId":"device-123","deviceName":"Desktop"}'

# Response sẽ có accessToken và refreshToken trong data

# 2. Lấy thông tin cá nhân
curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer <access_token>"

# 3. Cập nhật thông tin cá nhân
curl -X PUT http://localhost:8080/api/profile \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "email": "newemail@example.com",
    "phoneNumber": "0987654321"
  }'

# 4. Đổi mật khẩu (sẽ logout tất cả thiết bị)
curl -X PUT http://localhost:8080/api/profile \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{"password": "newpassword123"}'

# 5. Refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refresh_token>"}'
```

## 📝 Service Layer

### UserService

Quản lý người dùng:
- `createUser()` - Tạo user mới
- `updateUser()` - Cập nhật thông tin user
- `deleteUser()` - Xóa user
- `getUserById()`, `getUserByUsername()`, `getUserByEmail()` - Tìm user
- `addRoleToUser()`, `removeRoleFromUser()`, `updateUserRoles()` - Quản lý roles
- `clearAllRefreshTokens()` - Xóa tất cả refresh tokens của user (logout tất cả thiết bị)
- `createUserFromRequest()`, `updateUserFromRequest()` - CRUD với DTOs
- `updateProfile()` - Cập nhật thông tin cá nhân (chỉ user đó)
- `getCurrentUserProfile()` - Lấy thông tin user hiện tại

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

## 📦 Response Format

Tất cả API đều trả về format chuẩn `ApiResponse<T>`:

```json
{
  "code": 200,
  "message": "Thông báo",
  "status": "SUCCESS",
  "data": { ... }
}
```

### Các status codes:
- `200` - SUCCESS: Thành công
- `201` - CREATED: Tạo mới thành công
- `400` - BAD_REQUEST: Dữ liệu không hợp lệ
- `401` - UNAUTHORIZED: Chưa đăng nhập hoặc token không hợp lệ
- `403` - FORBIDDEN: Không có quyền truy cập
- `404` - NOT_FOUND: Không tìm thấy resource
- `409` - CONFLICT: Resource đã tồn tại (trùng username/email)
- `500` - INTERNAL_ERROR: Lỗi server

## 🐛 Exception Handling

Hệ thống có `GlobalExceptionHandler` để xử lý và trả về format `ApiResponse`:

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

