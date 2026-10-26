# Person Contact Manager (Spring Boot + Angular Standalone)

A clean, zero-boilerplate full-stack RESTful web service built with **Spring Boot 4 (Java 17)**, **Spring Data JPA**, an **H2 in-memory database**, and an **Angular (Standalone Components + Signals)** frontend.

---

## Project Structure

```text
person-contact-manager/
├── README.md                        # Setup, API reference, and usage guide
├── backend/                         # Spring Boot REST API (Port 8080)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/contacts/
│       │   ├── ContactManagerApplication.java  # Main entrypoint + initial seed data
│       │   ├── Person.java                     # JPA Entity
│       │   ├── PersonRepository.java           # Spring Data JPA Repository
│       │   └── PersonController.java           # Explicit @RestController (/api/people)
│       ├── main/resources/
│       │   └── application.properties          # H2 & server configuration
│       └── test/java/com/example/contacts/
│           └── PersonControllerTest.java       # Integration tests
└── frontend/                        # Angular Standalone Frontend (Port 4200)
    ├── package.json
    ├── angular.json
    └── src/
        ├── main.ts                  # Standalone bootstrap
        └── app/
            ├── app.config.ts        # HttpClient provider configuration
            ├── person.model.ts      # TypeScript interface
            ├── person.service.ts    # REST API client service
            ├── app.component.ts     # Standalone component with Angular Signals
            ├── app.component.html   # Template using @if and @for
            └── app.component.css    # Scoped styles
```

---

## REST API Endpoints

Base URL: `http://localhost:8080/api/people`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/people` | List all people (or filter via `?lastName=Turing`) | `200 OK` |
| `GET` | `/api/people/{id}` | Get a single person by ID | `200 OK`, `404 Not Found` |
| `POST` | `/api/people` | Create a new person | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/people/{id}` | Update an existing person | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/people/{id}` | Delete a person by ID | `204 No Content`, `404 Not Found` |

---

## Running the Application

### 1. Start the Spring Boot Backend

From the `backend/` folder:

```bash
cd backend
./mvnw spring-boot:run
```

- The REST API starts at `http://localhost:8080/api/people` with 3 sample contacts pre-seeded (`Ada Lovelace`, `Alan Turing`, `Grace Hopper`).
- **H2 Database Console**: Open `http://localhost:8080/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:contactsdb`
  - **User Name**: `sa`
  - **Password**: *(leave empty)*

### 2. Start the Angular Frontend

> **Prerequisite**: Ensure Node.js (v18.19+ or v20+) and `npm` are installed (for example via `sudo apt install nodejs npm` or `nvm`).

From the `frontend/` folder:

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200` in your browser to view, search, add, edit, and delete contacts.

---

## Quick `curl` Test Commands

```bash
# 1. List all people
curl -s http://localhost:8080/api/people

# 2. Search by last name (case-insensitive partial match)
curl -s "http://localhost:8080/api/people?lastName=tur"

# 3. Create a new person
curl -i -X POST http://localhost:8080/api/people \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Margaret","lastName":"Hamilton","email":"margaret@mit.edu","phone":"+1 617 253 1000"}'

# 4. Update a person (e.g. ID 1)
curl -i -X PUT http://localhost:8080/api/people/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ada","lastName":"King-Lovelace","email":"ada@analytical.engine","phone":"+44 20 7946 0101"}'

# 5. Delete a person (e.g. ID 1)
curl -i -X DELETE http://localhost:8080/api/people/1
```

