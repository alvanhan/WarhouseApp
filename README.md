# Warehouse Management API

REST API untuk manajemen gudang dengan fitur pengelolaan item, variant, dan stok. Dibangun dengan Spring Boot 3.x dan Java 17.

## Tech Stack

| Kategori | Teknologi | Versi |
|----------|-----------|-------|
| Language | Java | 17 (LTS) |
| Framework | Spring Boot | 3.2.x |
| Build Tool | Maven | 3.9.x |
| ORM | Spring Data JPA + Hibernate | Bundled |
| Database | H2 (In-Memory) | 2.x |
| Validation | Jakarta Validation | Bundled |
| API Documentation | SpringDoc OpenAPI (Swagger) | 2.5.x |
| DTO Mapping | MapStruct | 1.5.x |
| Boilerplate Reduction | Lombok | Bundled |
| Containerization | Docker + Docker Compose | Latest |
| Testing | JUnit 5 + Mockito | Bundled |

---

## Fitur Utama

- **Manajemen Item**: CRUD operasi untuk data item dengan harga dasar
- **Manajemen Variant**: Kelola variasi produk (ukuran, warna, dll) per item
- **Manajemen Stok**: Update kuantitas stok dan proses penjualan
- **Pagination & Filtering**: Semua list endpoint mendukung pagination, sorting, dan filter
- **Sell Operation**: Proses penjualan dengan pessimistic locking untuk mencegah race condition
- **Validasi Input**: Validasi request menggunakan Jakarta Validation
- **Global Error Handling**: Penanganan error yang konsisten dengan format response standar
- **API Documentation**: Swagger UI untuk dokumentasi

---

## Arsitektur

Service API menggunakan **Modular Layered Architecture** dengan struktur sebagai berikut:

```
src/main/java/com/warehouse/api/
├── WarehouseApiApplication.java        # Main application entry point
├── common/                             # Shared components
│   ├── config/                         # Configuration classes
│   ├── dto/                            # Common DTOs (ApiResponse, PaginatedResponse)
│   └── exception/                      # Custom exceptions & global handler
├── item/                               # Item module
│   ├── controller/                     # REST endpoints
│   ├── dto/                            # Request/Response DTOs
│   ├── entity/                         # JPA entity
│   ├── mapper/                         # MapStruct mapper
│   ├── repository/                     # Data access layer
│   └── service/                        # Business logic
├── variant/                            # Variant module (same structure)
├── stock/                              # Stock module (same structure)
└── seeder/                             # Data initializer for sample data
```

## Persyaratan Sistem

### Opsi A: Menggunakan Docker (Direkomendasikan)

- Docker Engine 20.10+
- Docker Compose 2.0+

### Opsi B: Menjalankan Manual

- Java Development Kit (JDK) 17 atau lebih tinggi
- Apache Maven 3.9.x atau lebih tinggi (opsional, sudah tersedia Maven Wrapper)

---

## Instalasi dan Menjalankan Aplikasi

### Langkah 1: Clone Repository

```bash
git clone <repository-url>
cd warehouse-api
```

### Langkah 2: Jalankan Aplikasi

#### Opsi A: Menggunakan Docker (Direkomendasikan)

Tidak perlu instalasi Java atau Maven. Cukup jalankan:

```bash
docker compose up --build
```

Untuk menjalankan di background:

```bash
docker compose up --build -d
```

Untuk menghentikan aplikasi:

```bash
docker compose down
```

#### Opsi B: Menggunakan Maven Wrapper

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

#### Opsi C: Build dan Jalankan JAR

```bash
# Build
./mvnw clean package -DskipTests

# Jalankan
java -jar target/warehouse-api-0.0.1-SNAPSHOT.jar
```

### Langkah 3: Verifikasi Aplikasi Berjalan

Setelah aplikasi berjalan, akses URL berikut:

| URL | Keterangan |
|-----|------------|
| http://localhost:8080/swagger-ui/index.html | Swagger UI - Dokumentasi API interaktif |
| http://localhost:8080/h2-console | H2 Console - Akses database |
| http://localhost:8080/v3/api-docs | OpenAPI Schema (JSON) |
| http://localhost:8080/api/v1/items | API endpoint untuk items |

#### H2 Database Console

Untuk mengakses H2 console, gunakan kredensial berikut:

```
JDBC URL: jdbc:h2:mem:warehousedb
Username: sa
Password: (kosongkan)
Driver Class: org.h2.Driver
```

> **PENTING**: Jika mendapat error "Database '/root/test' not found", pastikan JDBC URL **persis** seperti di atas.
> 

---

## Testing

Aplikasi dilengkapi dengan **23 unit tests** yang mencakup semua service layer.

### Run Tests

```bash
./run-tests.sh
```

### Test Coverage

| Module | Test File | Test Cases |
|--------|-----------|------------|
| Item Service | ItemServiceTest.java | 7 tests |
| Variant Service | VariantServiceTest.java | 7 tests |
| Stock Service | StockServiceTest.java | 9 tests |
| **Total** | **3 files** | **23 tests** |

**Framework**: JUnit 5 + Mockito  
**Last Run**: All tests passed

---