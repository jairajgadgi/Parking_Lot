# Parking Lot LLD

Spring Boot + JPA implementation of a parking lot low-level design.

## What is included
- Parking lot, floor, slot, gate, vehicle, ticket, and payment entities
- Admin APIs to create lots, floors, slots, and gates
- Entry and exit flow APIs
- Availability lookup by vehicle type
- In-memory H2 database for local testing
- MySQL profile for persistent local/dev runs

## Main APIs

### Admin setup
- `POST /api/parking-lots`
- `POST /api/parking-lots/{parkingLotId}/floors`
- `POST /api/floors/{floorId}/slots`
- `POST /api/parking-lots/{parkingLotId}/gates`

### Parking flow
- `POST /api/parking/entry`
- `POST /api/parking/exit`
- `GET /api/tickets/{ticketId}`
- `GET /api/parking-lots/{parkingLotId}/availability?vehicleType=FOURWHEELER`

## Run with H2 (default)
```powershell
mvnw.cmd test
mvnw.cmd spring-boot:run
```

This uses `application.properties` + `application-h2.properties`.

## Run with MySQL
Start MySQL via Docker:

```powershell
docker compose up -d
```

Then run the app with the MySQL profile:

```powershell
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

This uses `application.properties` + `application-mysql.properties`.

## Switch profiles manually

If you want to run a specific profile explicitly:

```powershell
$env:SPRING_PROFILES_ACTIVE="h2"; .\mvnw.cmd spring-boot:run
$env:SPRING_PROFILES_ACTIVE="mysql"; .\mvnw.cmd spring-boot:run

## Example entry request
```json
{
  "parkingLotId": 1,
  "licensePlate": "KA01AB1234",
  "vehicleType": "FOURWHEELER"
}
```

## Example exit request
```json
{
  "ticketId": 1,
  "paymentMode": "UPI"
}
```

