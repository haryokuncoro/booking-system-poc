# 📌 Booking System POC with Kafka + SSE + DB Lock

## 1. **Deskripsi Sistem**

Sistem ini adalah POC **Booking System** yang aman dari **double booking**, scalable, dan user-friendly:

* **Async Booking**: request booking masuk ke **Kafka queue**
* **DB Lock**: menggunakan **pessimistic lock / optimistic lock** untuk mencegah race condition
* **Real-time status**: user mendapat update via **SSE**

---

## 2. **Tech Stack**

* Java 17 + Spring Boot 3
* Spring Data JPA + MySQL (H2 untuk testing)
* Kafka + Zookeeper
* SSE (Server-Sent Events) untuk real-time status

---

## 3. **Cara Test Race Condition**

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

3. **Periksa hasil**:

* Database → stok tidak minus, status booking SUCCESS/FAILED sesuai
* Kafka consumer logs → proses satu per satu
* SSE → real-time status update
