# 🚀 One Project One Month (1P1M) — Backend

<img width="640" height="640" alt="One Project One Month Logo" src="https://github.com/user-attachments/assets/a9be4bc7-add1-4e96-8ef7-6e885e5dbc0f" />

The **One Project One Month (1P1M)** backend powers the community platform where developers and tech enthusiasts can build, share, and collaborate on one project every month — helping each other grow through learning and contribution.

> 💡 *Code. Create. Connect. Every month.*

---

## 🧩 Overview

This backend service provides RESTful APIs for:
- 🧑‍💻 **User authentication and authorization** (JWT + OAuth2 GitHub Login)
- 📄 **Profile management**
- 💡 **Project submission and idea sharing**
- 🗓️ **Challenge registration**
- 💬 **Community interaction**

It is designed to be **secure**, **scalable**, and **developer-friendly**, integrating seamlessly with the React frontend.

---

## ⚙️ Tech Stack

| Category | Technologies |
|-----------|--------------|
| **Language** | ☕ Java |
| **Framework** | Spring Boot |
| **Security** | Spring Security + JWT |
| **Database** | PostgreSQL |
| **Cloud Services** | Cloudinary (media) |
| **Build Tool** | Maven |
| **Version Control** | Git & GitHub |

---

## 🏗️ Architecture Overview

```
React Frontend  →  Spring Boot Backend  →  PostgreSQL Database
                         ↓
                   Cloudinary (Media)
```

The backend follows a **modular architecture**, with clear separation between:
- `controller` → request handling
- `service` → business logic
- `repository` → database operations
- `model` → JPA entities and DTOs

---

## 🚀 Getting Started

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/one-project-one-month/1P1M_Portfolio_Backend.git
cd 1P1M_Portfolio_Backend
```

### 2️⃣ Configure the Environment
Update `application.properties` (or `application.yml`) with your local database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/1p1m_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3️⃣ Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

The server will start at:
👉 **http://localhost:8080**

---

## 🔐 Authentication Flow

1. **Sign Up / Sign In** with email or GitHub OAuth2  
2. **License & user validation**  
3. **JWT token generation** upon successful login  
4. **Token verification** for all secured endpoints  

---

## 📦 Folder Structure

```
src/
 ├── main/
 │   ├── java/com/_p1m/portfolio/
 │   │   ├── controller/
 │   │   ├── service/
 │   │   ├── repository/
 │   │   ├── model/
 │   │   └── config/
 │   └── resources/
 │       ├── templates/
 │       ├── application.properties
 │       └── static/
 └── test/
```

---

## 🧠 Future Enhancements

- 🕒 Task scheduler for monthly project resets  
- 💬 Comment and feedback API  
- 📈 Analytics and leaderboard endpoints  
- ☁️ Deployment via AWS & Docker  

---

## 🧑‍🤝‍🧑 Contributors

| Name | Role | GitHub |
|------|------|--------|
| Min Zayar Maung | Project Lead / Backend Leader | [@MinZayarMaung](https://github.com/MinZayarMaung) |
| Hlyam Htet Kyaw | Backend Leader | [@HlyamHtetKyaw](https://github.com/HlyamHtetKyaw) |
| Community Contributors | Coming Soon | 🚀 |

---

## 🏁 License

This project is licensed under the **MIT License** — free to use and contribute.

---

> ❤️ *“The best way to learn is to build — together.”*  
> — *One Project One Month Team*
