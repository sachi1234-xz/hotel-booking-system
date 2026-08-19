# Booking Service

Spring Boot REST API for hotel bookings, backed by MongoDB.

## Endpoints

| Method | Path                          | Description              |
|--------|-------------------------------|---------------------------|
| GET    | /api/bookings                 | List all (or filter by `roomNumber` / `guestName`) |
| GET    | /api/bookings/{id}            | Get one booking          |
| POST   | /api/bookings                 | Create a booking         |
| PUT    | /api/bookings/{id}            | Update a booking         |
| PATCH  | /api/bookings/{id}/status     | Update status (`?status=CHECKED_IN`) |
| DELETE | /api/bookings/{id}            | Delete a booking         |
| GET    | /actuator/health              | Health check              |

## Run with Docker (app + MongoDB)

```bash
docker compose up --build
```

App will be available at `http://localhost:8080`, MongoDB at `localhost:27020`.

To stop: `docker compose down` (add `-v` to also wipe the Mongo volume).

## Run locally without Docker

Start a local MongoDB (or use `docker run -p 27020:27017 mongo:7`), then:

```bash
./mvnw spring-boot:run
```

## Test with Postman

1. Open Postman → **Import** → select `postman/booking-service.postman_collection.json`.
2. Run **Create Booking** first — it auto-saves the returned `id` into the `bookingId` collection variable.
3. Run the rest of the requests in any order (Get, Update, Update Status, Delete).
4. `baseUrl` defaults to `http://localhost:8080` — change it in the collection variables if needed.

## Push to GitHub

```bash
cd booking-service
git init
git add .
git commit -m "Add MongoDB booking API, Docker, and Postman collection"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

If the repo doesn't exist yet, create it first on GitHub (or via `gh repo create <your-repo> --public --source=. --remote=origin`).
