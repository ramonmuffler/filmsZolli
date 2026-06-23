# Films

Projekt fuer eine Film- und Serienverwaltung mit Java Spring Boot, MongoDB und React.

## Stack

- Backend: Spring Boot, Spring Web MVC, Validation
- Datenzugriff: Spring Data MongoDB Repositories
- Datenbank: MongoDB, Datenbank `filme_serien_db`
- Frontend: React mit Vite

JPA ist fuer relationale Datenbanken gedacht. Da das Projekt laut Auftrag spaeter mit MongoDB arbeiten soll, nutzt das Backend hier Spring Data MongoDB. Die Repository-Schicht ist bewusst aehnlich aufgebaut wie eine JPA-Schicht, passt aber technisch korrekt zu MongoDB.

## Starten

MongoDB starten:

```powershell
docker compose up -d
```

Backend starten:

```powershell
.\mvnw.cmd spring-boot:run
```

Frontend starten:

```powershell
cd frontend
pnpm install
pnpm dev
```

Danach:

- Backend: http://localhost:8080
- Frontend: http://localhost:5173
- MongoDB: localhost:27018

## API

- `GET|POST /api/contents`
- `GET|PUT|DELETE /api/contents/{id}`
- `GET|POST /api/categories`
- `GET|PUT|DELETE /api/categories/{id}`
- `GET|POST /api/users`
- `GET|PUT|DELETE /api/users/{id}`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET|POST /api/ratings`
- `GET|PUT|DELETE /api/ratings/{id}`
- `GET|POST /api/watched`
- `GET|PUT|DELETE /api/watched/{id}`

Filter:

- `GET /api/contents?categoryId=...`
- `GET /api/ratings?contentId=...`
- `GET /api/ratings?userId=...`
- `GET /api/watched?contentId=...`
- `GET /api/watched?userId=...`

## MongoDB

Standardverbindung aus `application.properties`:

```text
mongodb://database_user:sicheres_passwort@localhost:27018/filme_serien_db?authSource=filme_serien_db
```

Alternativ kann die Verbindung mit `MONGODB_URI` ueberschrieben werden.
