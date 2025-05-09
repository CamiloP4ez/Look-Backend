# 📚 Look API

This is the backend API for the Look project, built with Spring Boot and MongoDB.

---

## 🚀 Requirements

- Java 17+
- Maven 3.8+
- Docker (for running MongoDB container)
- MongoDB Client (optional, for manual inspection)

---

## 🧪 MongoDB Setup (Docker)

You must run a MongoDB instance in a Docker container with the following settings:

- **Port:** `27017` (default)
- **Username:** `admin`
- **Password:** `admin2224`

Run the following command to start the MongoDB container:

```bash
docker run -d \
  --name look-mongo \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin2224 \
  mongo
