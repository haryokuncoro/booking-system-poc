# 📌 README: Booking System POC with Kafka + SSE + DB Lock

## 1. **Deskripsi Sistem**

Sistem ini adalah POC **Booking System** yang aman dari **double booking**, scalable, dan user-friendly:

* **Async Booking**: request booking masuk ke **Kafka queue**
* **DB Lock**: menggunakan **pessimistic lock / optimistic lock** untuk mencegah race condition
* **Real-time status**: user mendapat update via **SSE**
* **Security**: JWT auth, input validation
* **Performance & Reliability**: Kafka partition, idempotency, retry mechanism

---

## 2. **Tech Stack**

* Java 17 + Spring Boot 3
* Spring Data JPA + MySQL (H2 untuk testing)
* Kafka + Zookeeper
* SSE (Server-Sent Events) untuk real-time status
* JWT (JSON Web Token) untuk authentication

---

## 3. **Struktur Project**

```
src/
 └── main/
     ├── java/com/example/booking/
     │    ├── entity/
     │    │    ├── Product.java
     │    │    └── Booking.java
     │    ├── repository/
     │    │    ├── ProductRepository.java
     │    │    └── BookingRepository.java
     │    ├── service/
     │    │    ├── BookingService.java
     │    │    ├── BookingPublisher.java
     │    │    └── BookingNotificationService.java
     │    ├── consumer/
     │    │    └── BookingConsumer.java
     │    ├── controller/
     │    │    └── BookingController.java
     │    └── config/
     │         └── KafkaConfig.java
     └── resources/
          └── application.properties
```

---

## 4. **Langkah-Langkah Membuat Sistem dari Awal**

### a. **Setup Project & Dependencies**

Tambahkan di `pom.xml`:

```xml
<!-- Web, JPA, MySQL/H2, Kafka, JWT -->
<dependencies>
    <!-- Spring Boot Starter Web, Data JPA -->
    <!-- Kafka -->
    <!-- JWT -->
    <!-- Lombok (opsional) -->
</dependencies>
```

### b. **Buat Entity**

* `Product.java` → id, name, stock, price
* `Booking.java` → id, productId, quantity, status (QUEUED/SUCCESS/FAILED), createdAt, version

### c. **Repository**

* `ProductRepository` → `@Lock(PESSIMISTIC_WRITE)` untuk DB lock
* `BookingRepository` → CRUD booking

### d. **Kafka Configuration**

* Topik: `booking-topic`
* Producer & Consumer setup
* Partition: minimal 3 untuk parallelism

### e. **Service Layer**

* `BookingService`: createBooking, processBooking (DB lock), getStatus
* `BookingPublisher`: publish bookingId ke Kafka
* `BookingNotificationService`: SSE emitter untuk real-time status

### f. **Kafka Consumer**

* Ambil bookingId → processBooking → update status → SSE notification
* Idempotent: jika booking sudah SUCCESS/FAILED, jangan proses ulang

### g. **Controller**

* POST `/api/booking/{productId}?quantity=X` → enqueue booking
* GET `/api/booking/status/stream/{bookingId}` → SSE stream real-time status

### h. **Security**

* JWT auth untuk semua endpoint
* Input validation: quantity >0, productId valid
* SSE hanya untuk bookingId milik user

---

## 5. **Cara Test Race Condition**

1. **Setup Produk dengan stok rendah**:

```sql
INSERT INTO product(name, stock, price) VALUES ('Ticket A', 5, 100000);
```

2. **Simulasikan banyak request bersamaan**:

* **Bash/cURL**:

```bash
for i in {1..5}; do
  curl -X POST "http://localhost:8080/api/booking/1?quantity=3" &
done
wait
```

* **Java Multi-threading**:
  Gunakan `IntStream.range(0,5).parallel()` untuk mengirim request bersamaan

* **Postman / JMeter / Locust** untuk load testing tinggi

3. **Periksa hasil**:

* Database → stok tidak minus, status booking SUCCESS/FAILED sesuai
* Kafka consumer logs → proses satu per satu
* SSE → real-time status update

4. **Tips memaksa race condition terlihat**:

```java
@Transactional
public void processBooking(Booking booking){
    try { Thread.sleep(500); } catch(Exception e){} // simulasi delay
    ...
}
```

---

## 6. **Tips Performance & Security**

* Kafka partitioning & consumer group → parallelism
* Retry & dead-letter queue untuk reliability
* Idempotency → bookingId unik, jangan proses ulang
* Connection pooling (HikariCP)
* JWT auth + HTTPS
* Input validation & SSE access control

---

## 7. **Optional Improvements**

* Caching stok di Redis untuk read-heavy scenario
* Batch processing di Kafka consumer
* Metrics & monitoring (Prometheus / Grafana)

---

## 8. **Docker Compose (Opsional)**

Untuk testing cepat:

```yaml
version: '3'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports: ["2181:2181"]
  kafka:
    image: wurstmeister/kafka
    ports: ["9092:9092"]
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: bookingdb
    ports: ["3306:3306"]
```

* Jalankan Spring Boot → terhubung ke Kafka & MySQL
