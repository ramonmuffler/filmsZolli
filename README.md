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
- Beispiel-Login in der App: `timomaag` / `passwort123` oder `ramonmuffler` / `passwort123`

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
- `GET|POST /api/favorites`
- `GET|PUT|DELETE /api/favorites/{id}`

Filter:

- `GET /api/contents?categoryId=...`
- `GET /api/contents?type=FILM`
- `GET /api/contents?type=SERIE`
- `GET /api/contents?minRating=4.5`
- `GET /api/ratings?contentId=...`
- `GET /api/ratings?userId=...`
- `GET /api/watched?contentId=...`
- `GET /api/watched?userId=...`
- `GET /api/favorites?contentId=...`
- `GET /api/favorites?userId=...`

## MongoDB

Standardverbindung aus `application.properties`:

```text
mongodb://database_user_readwrite:write_passwort@localhost:27018/filme_serien_db?authSource=filme_serien_db
```

Alternativ kann die Verbindung mit `MONGODB_URI` ueberschrieben werden.

Compass-Verbindungen:

- Nur lesen: `mongodb://database_user_read:read_passwort@localhost:27018/filme_serien_db?authSource=filme_serien_db`
- Lesen und schreiben: `mongodb://database_user_readwrite:write_passwort@localhost:27018/filme_serien_db?authSource=filme_serien_db`
- Admin/Root: `mongodb://root:root_password@localhost:27018/?authSource=admin`

Die App legt beim Start Beispieldaten an, falls sie fehlen. Enthalten sind die Collections
`users`, `contents`, `categories`, `ratings`, `watched` und `favorites`.

## Backup und Restore

Backup im MongoDB-Container:

```powershell
docker exec films-mongodb mongodump --uri "mongodb://database_user_read:read_passwort@localhost:27017/filme_serien_db?authSource=filme_serien_db" --out /tmp/filme_serien_backup_2026-06-30
```

Restore im MongoDB-Container:

```powershell
docker exec films-mongodb mongorestore --uri "mongodb://database_user_readwrite:write_passwort@localhost:27017/filme_serien_db?authSource=filme_serien_db" --drop /tmp/filme_serien_backup_2026-06-30/filme_serien_db
```
