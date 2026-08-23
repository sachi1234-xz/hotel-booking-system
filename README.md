# Hotel Booking System

A full-stack microservices-based hotel booking platform built with Spring Boot, React, and Docker.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2.2, Spring Cloud Gateway |
| Frontend | React 19, Tailwind CSS 4, Vite 8 |
| Database | PostgreSQL 16 (prod), H2 (dev) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Auth | JWT (jjwt 0.12.6), BCrypt |
| Deployment | Docker, Docker Compose |

## System Architecture

```
Client (React :3000) --> Nginx --> API Gateway (:8080) --> Microservices (:8081-8084)
```

The API Gateway is the single entry point for all client requests. It handles JWT validation, API key injection, routing, and CORS.

## Microservices

| Service | Port | Database | API Key | Description |
|---------|------|----------|---------|-------------|
| Auth Service | 8081 | PostgreSQL | `AUTH_SECRET_12345` | User registration, login, JWT |
| Hotel Service | 8082 | H2 / PostgreSQL | `HOTEL_SECRET_67890` | Hotel & room management |
| Booking Service | 8083 | H2 / PostgreSQL | `BOOKING_SECRET_11111` | Room booking |
| Payment Service | 8084 | H2 / PostgreSQL | `PAYMENT_SECRET_22222` | Payment processing & invoicing |
| API Gateway | 8080 | - | - | Routing, auth, API key injection |
| Frontend | 3000 | - | - | React SPA |
| Auth DB | 5432 | PostgreSQL | - | Persistent auth storage |

## API Endpoints

### Auth Service (`/api/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT |
| GET | `/api/auth/validate` | Validate JWT token |

### Hotel Service (`/api/hotels` & `/api/rooms`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hotels` | List all hotels |
| GET | `/api/hotels/{id}` | Get hotel by ID |
| POST | `/api/hotels` | Create hotel |
| PUT | `/api/hotels/{id}` | Update hotel |
| DELETE | `/api/hotels/{id}` | Delete hotel |
| POST | `/api/hotels/{hotelId}/rooms` | Add room to hotel |
| GET | `/api/hotels/{hotelId}/rooms` | Get hotel's rooms |
| GET | `/api/rooms/available` | Get available rooms |
| GET | `/api/rooms/{id}` | Get room by ID |
| PUT | `/api/rooms/{id}` | Update room |
| DELETE | `/api/rooms/{id}` | Delete room |

### Booking Service (`/api/bookings`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings` | Create booking |
| GET | `/api/bookings/my` | Get current user's bookings |
| GET | `/api/bookings/{id}` | Get booking by ID |
| GET | `/api/bookings/user/{userId}` | Get bookings for user |
| DELETE | `/api/bookings/{id}` | Cancel booking |

### Payment Service (`/api/payments`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/process` | Process payment |
| GET | `/api/payments/my` | Get current user's payments |
| GET | `/api/payments/history?userId=` | Payment history |
| GET | `/api/payments/{id}` | Get payment by ID |
| GET | `/api/payments/{id}/invoice` | Get invoice |

## Getting Started

### Prerequisites
- Docker & Docker Compose

### Run the System

```bash
# Clone the repository
git clone https://github.com/sachi1234-xz/hotel-booking-system.git
cd hotel-booking-system

# Build and start all services
docker compose up -d --build

# View logs
docker compose logs -f
```

### Access the Application
- **Frontend:** http://localhost:3000
- **API Gateway:** http://localhost:8080
- **Auth Swagger:** http://localhost:8081/swagger-ui.html
- **Hotel Swagger:** http://localhost:8082/swagger-ui.html
- **Booking Swagger:** http://localhost:8083/swagger-ui.html
- **Payment Swagger:** http://localhost:8084/swagger-ui.html

## How to Use

1. **Register** a new account at http://localhost:3000/register
2. **Login** with your credentials
3. **Browse** hotels and available rooms
4. **Book** a room by selecting dates and confirming
5. **Pay** for your booking using CARD, PAYPAL, or BANK_TRANSFER
6. **View** your bookings and payments in the dashboard

## Project Structure

```
hotel-booking-system/
├── auth-service/          # User authentication & JWT
├── hotel-service/         # Hotel & room management
├── booking-service/       # Room booking
├── payment-service/       # Payment processing
├── api-gateway/           # Request routing & security
├── frontend/              # React SPA
├── init-db/               # Database init scripts
├── docker-compose.yml     # Unified deployment
└── README.md
```

## Security

- **JWT Authentication:** Tokens issued by auth-service, validated by API Gateway
- **API Keys:** Each service has a unique key injected by the gateway
- **Multi-layered:** Gateway filters -> Service-level API key filters -> Spring Security
- **Password Hashing:** BCrypt for secure password storage

## Sample Data

The hotel service pre-loads sample data:
- **Cinnamon Grand Colombo** (Colombo) - 3 rooms
- **Earls Regency Kandy** (Kandy) - 2 rooms

## Group Members

| Name | Student ID | Role |
|------|-----------|------|
| Sanuka Vithanage | ITBIN-2313-0118 | Auth Service |
| Mulani Yohansa | ITBIN-2312-0019 | Hotel Service |
| Amaya Mihindusiri | ITBIN-2313-0065 | Payment Service |
| Sachini Sudeshika | ITBIN-2313-0091 | API Gateway |
| Maleesha Nethmini | ITBIN-2313-0065 | Booking Service |

## License

This project is for educational purposes.
