# RevConnect P2

RevConnect is a Spring Boot social networking web application with role-based user experiences (`PERSONAL`, `CREATOR`, `BUSINESS`).

## Tech Stack
- Java 21
- Spring Boot 3.2.3
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA (Hibernate)
- SQL
- Maven

## Prerequisites
- JDK 21
- Maven 3.9+

## Run Locally
1. Clone the repository.
2. Open a terminal in project root.
3. Run:

```bash
mvn spring-boot:run
```

App URL:
- `http://localhost:8083`

Login page:
- `http://localhost:8083/login`

## Database Configuration
Current setup uses file-based H2 so data is preserved across restarts.

Configured in:
- `src/main/resources/application.properties`

Key property:

```properties
spring.datasource.url=jdbc:h2:file:./data/revconnectdb;MODE=Oracle;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE
```

H2 console:
- `http://localhost:8083/h2-console`

## Test
Run all tests:

```bash
mvn test
```

## Main Features
- User registration/login/logout
- Role-based UI and access control
- Feed and post creation (text/image)
- Likes, shares, comments
- Profile management
- Follow/network workflows
- Business page and product catalog (business accounts)
- Search users and hashtags
- Notifications and preferences

## Project Structure
```text
RevConnect_P2/
├─ src/
│  ├─ main/
│  │  ├─ java/com/rev/app/
│  │  │  ├─ config/                # Security + app config
│  │  │  ├─ controller/            # MVC controllers
│  │  │  ├─ rest/                  # REST controllers
│  │  │  ├─ service/               # Business logic
│  │  │  ├─ repository/            # Data access layer
│  │  │  ├─ entity/                # JPA entities
│  │  │  ├─ dto/                   # DTO classes
│  │  │  └─ mapper/                # Mapper classes
│  │  └─ resources/
│  │     ├─ templates/             # Thymeleaf templates
│  │     ├─ static/css/            # Styles
│  │     ├─ static/js/             # Scripts
│  │     └─ application.properties # App settings
│  └─ test/java/com/rev/app/       # Tests
├─ uploads/                         # Uploaded files (runtime)
├─ data/                            # H2 file DB (runtime)
├─ logs/                            # Logs (runtime)
├─ pom.xml
└─ README.md
```

## Notes
- If port `8083` is in use, stop the existing process or change `server.port` in `application.properties`.
