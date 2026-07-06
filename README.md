# intern-auth-project

A full-stack mobile authentication system built during an internship at BEL (Bharat Electronics Limited). The app demonstrates a complete login flow — from a Flutter mobile frontend to a secured Java backend — using industry-standard tools and practices.

---

## What It Does

1. User opens the app and sees a login screen
2. User enters their username and password
3. The app sends the credentials to the backend API
4. The backend verifies them against the database
5. If valid — a JWT token is issued and the user lands on the home screen
6. If invalid — an error message is shown
7. The JWT token expires after 30 minutes, preventing indefinite sessions

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Mobile App | Flutter + Dart | Cross-platform UI (Android & iOS from one codebase) |
| Backend | Java 17 + Spring Boot | REST API that handles login logic |
| Authentication | JWT (JSON Web Tokens) | Secure, stateless session management |
| Database | PostgreSQL on Neon | Stores user credentials (passwords are hashed) |
| Password Security | BCrypt | One-way hashing — plain text passwords are never stored |
| Containerisation | Docker | Packages the backend into a portable, consistent environment |
| Backend Deployment | Railway | Hosts the Dockerised backend with a public URL |
| API Testing | Postman | Used during development to test all endpoints |
| Version Control | Git + GitHub | Source code management and collaboration |

---

## Project Structure

```
intern-auth-project/
├── backend/                  # Spring Boot application
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/authservice/
│   │       │   ├── AuthController.java     # Handles POST /login
│   │       │   ├── User.java               # Database entity
│   │       │   ├── UserRepository.java     # Database queries
│   │       │   ├── JwtUtil.java            # Token generation & validation
│   │       │   ├── CorsConfig.java         # Allows Flutter to call the API
│   │       │   └── AppConfig.java          # Password encoder setup
│   │       └── resources/
│   │           └── application.properties  # Config (DB URL, JWT secret, port)
│   └── Dockerfile                          # Container recipe for Railway
└── frontend/                 # Flutter application
    └── lib/
        └── main.dart         # Login screen, home screen, API call logic
```

---

## How to Test This on Your Laptop

You can test either just the backend (using Postman) or the full app (running Flutter locally). Both options are described below.

### Prerequisites

Make sure you have these installed:

- [Java 17 JDK](https://adoptium.net)
- [Maven](https://maven.apache.org/install.html) (or use the `./mvnw` wrapper included)
- [Flutter SDK](https://docs.flutter.dev/get-started/install)
- [Git](https://git-scm.com)
- [Postman](https://www.postman.com/downloads/) (optional, for API testing)

---

### Step 1 — Clone the repository

```bash
git clone https://github.com/shree-sagar/intern-auth-project.git
cd intern-auth-project
```

---

### Step 2 — Test the backend API (Postman)

The backend is already live on Railway. You do not need to run it locally — just send requests to the deployed URL.

**Base URL:**
```
https://intern-auth-project-production.up.railway.app/login
```

**Test a valid login:**

```
Method:  POST
URL:     https://intern-auth-project-production.up.railway.app/login
Headers: Content-Type: application/json
Body:
{
  "username": "testuser",
  "password": "password123"
}
```

**Expected success response (HTTP 200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Expected failure response (HTTP 401):**
```json
{
  "message": "Invalid credentials"
}
```

---

### Step 3 — Run the Flutter app locally

```bash
cd frontend

# Install dependencies
flutter pub get

# Run as a Windows desktop app (no emulator needed)
flutter run -d windows

# OR run on a connected Android device
flutter run -d android

# OR run on a chrome
flutter run -d chrome
```

The app will launch. Enter `testuser` / `password123` to log in successfully.

> The app is already pointed at the live Railway backend URL, so no local backend setup is needed.

---

### Step 4 — (Optional) Run the backend locally

Only needed if you want to modify and test backend changes.

```bash
cd backend
```

Create a `.env` file or update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<neon-host>/<dbname>
spring.datasource.username=<your-neon-username>
spring.datasource.password=<your-neon-password>
spring.jpa.hibernate.ddl-auto=update
jwt.secret=<your-jwt-secret>
server.port=8080
```

Then run:

```bash
./mvnw spring-boot:run
```

Backend starts at `http://localhost:8080`. Update the URL in `frontend/lib/main.dart` to `http://10.0.2.2:8080/login` if testing on an Android emulator, or `http://localhost:8080/login` for Windows desktop.

---

## API Reference

### POST /login

Authenticates a user and returns a JWT token.

**Request**
```
POST /login
Content-Type: application/json
```
```json
{
  "username": "string",
  "password": "string"
}
```

**Success — 200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.abc123"
}
```

**Failure — 401 Unauthorized**
```json
{
  "message": "Invalid credentials"
}
```

---

## How JWT Works in This Project

```
1. User logs in with correct credentials
2. Backend signs a token:  { username, issued_at, expires_at } + secret key
3. Token is sent to Flutter
4. Token is valid for 30 minutes
5. After 30 minutes the token expires — user must log in again
6. Nobody can fake a token without knowing the secret key
```

The token is signed using HMAC-SHA256. The secret key lives only on the server (in Railway's environment variables), never in the app.

---

## How Docker Is Used

The backend is packaged into a Docker image before being deployed to Railway. This means:

- Railway runs the exact same environment every time
- No "works on my machine" problems
- The Dockerfile defines every dependency the backend needs

```dockerfile
# Build stage — compiles the Java project
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Run stage — runs only the compiled JAR (smaller image)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Download the App

The Android APK is available under [**Releases**](../../releases/latest) — no Play Store needed.

1. Download `app-release.apk`
2. On your Android phone, enable **Install from unknown sources** in Settings
3. Open the downloaded file to install
4. Log in with the test credentials below

---

## Test Credentials

| Username | Password |
|----------|----------|
| testuser | password123 |

---

## Key Concepts Explained

**Flutter** — Google's framework for building mobile apps. You write one codebase in Dart and it runs on Android, iOS, Windows, and more. We used it for the login screen and home screen.

**Spring Boot** — A Java framework that makes building REST APIs quick. Instead of writing boilerplate server code, Spring Boot handles routing, dependency injection, and configuration automatically.

**REST API** — A way for two applications to talk over HTTP. Flutter sends a POST request; Spring Boot responds with JSON. Same technology your browser uses to talk to websites.

**JWT (JSON Web Token)** — A signed string that proves who you are without the server needing to store session data. Think of it as a tamper-proof visitor badge that expires after 30 minutes.

**BCrypt** — A hashing algorithm for passwords. When a user is created, their password is hashed (scrambled one-way). On login, BCrypt re-scrambles what they typed and compares — the original password is never stored or compared directly.

**PostgreSQL on Neon** — Neon is a serverless PostgreSQL provider. We used it instead of running a local database so the backend can access user data from anywhere, including Railway's servers.

**Docker** — Packages an application and everything it needs (Java, dependencies, config) into a single container image. Run it anywhere and it behaves identically.

**Railway** — A cloud platform that hosts our Dockerised Spring Boot app and gives it a public HTTPS URL. Similar to Heroku — push your code (or Docker image) and it handles the rest.

---