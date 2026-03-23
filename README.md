# 🛒 OmniShop – AI Powered E-Commerce Platform

🚀 **Live Application**
👉 Frontend: https://omnishop-frontend.netlify.app/
👉 Backend API: https://omnishop-backend-1.onrender.com

---

## 📌 Overview

OmniShop is a **full-stack AI-powered e-commerce platform** built using **Spring Boot + React**.
It simulates a real-world scalable system with authentication, payments, AI chatbot, and inventory management.

---

## 🔥 Key Features

### 🔐 Authentication & Security

* JWT-based authentication
* Role-based access control (Admin/User)
* Protected routes (Cart, Wishlist, Orders)
* बिना login के cart/wishlist access नहीं

---

### 🛍️ E-Commerce Functionality

* Product listing & search (category + name)
* Add to Cart & Wishlist
* Admin Panel:

  * Add / Update Products
  * Restock Inventory
* Smart Inventory Handling:

  * Order place से पहले stock check
  * Out of stock होने पर suggestion देता है

---

### 🤖 AI Chatbot (Smart Assistant)

Users can ask:

* “Mujhe running shoes chahiye”
* “Best product under 2000”
* “In stock products dikhao”

Bot provides:

* ✅ Product suggestions
* ✅ Price details
* ✅ Stock availability

---

### 💳 Payment Integration

* Razorpay (Test Mode)

---

### 🔔 Notifications

* Firebase Push Notifications for new product updates

---

## ⚙️ Backend Highlights

* Spring Boot (Monolith architecture)
* JWT Authentication
* Rate Limiting using Redis (Bucket Algorithm)
* Circuit Breaker (Resilience4j)
* Previously built in Microservices (API Gateway + Service Discovery)

---

## 🎨 Frontend

* React.js
* Tailwind CSS
* Responsive UI

---

## 🧠 AI Integration

* Vector Store: Supabase
* Embeddings: Jina API
* LLM: Groq API

---

## 🗄️ Database

* Supabase (PostgreSQL)

---

## 🛠️ Tech Stack

* Backend: Spring Boot
* Frontend: React + Tailwind CSS
* Database: Supabase
* Hosting: Netlify (Frontend), Render (Backend)
* AI: Groq API + Jina API

---

## 🔑 Test User Credentials

```bash
Email: Tushar12@gmail.com  
Password: aloo@123
```

---

## 🔄 Application Flow

1. User Login
2. Browse / Search Products
3. Add to Cart / Wishlist
4. Checkout:

   * Inventory check
   * If insufficient → Suggest reduced quantity
5. Payment via Razorpay
6. Order placed successfully

---
---

## 🚀 Future Enhancements

* Docker deployment
* Kubernetes scaling
* Recommendation engine
* Admin analytics dashboard

---

## 🙌 Author

Tushar Handa
