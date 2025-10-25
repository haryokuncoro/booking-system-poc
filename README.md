
# Booking System POC with Kafka + SSE + DB Lock

## 1. **System Description**

This is a POC **Booking System** designed to be **safe from double booking**, scalable, and user-friendly:

* **Async Booking**: booking requests are sent to a **Kafka queue**
* **DB Lock**: uses **pessimistic lock / optimistic lock** to prevent race conditions
* **Real-time Status**: users receive updates via **SSE**

---

## 2. **Tech Stack**

* Java 17 + Spring Boot 3
* Spring Data JPA + MySQL (H2 for testing)
* Kafka + Zookeeper
* SSE (Server-Sent Events) for real-time status

---

## 3. **How to Test Race Conditions**

1. **Setup a product with low stock**:

```sql
INSERT INTO product(name, stock, price) VALUES ('Ticket A', 5, 100000);
```

2. **Simulate multiple concurrent requests**:

* **Bash/cURL**:

```bash
for i in {1..5}; do
  curl -X POST "http://localhost:8080/api/booking/1?quantity=3" &
done
wait
```

3. **Check the results**:

* Database → stock does not go negative, booking status SUCCESS/FAILED as expected
* Kafka consumer logs → processes requests one by one
* SSE → real-time status updates
