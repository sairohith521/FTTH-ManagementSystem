<p align="center">
  <img src="https://img.shields.io/badge/FTTH-Management%20System-0078D4?style=for-the-badge&logo=fiber&logoColor=white" alt="FTTH Management System"/>
</p>

<h1 align="center">🌐 FTTH Management System</h1>

<p align="center">
  <em>A full-stack Fiber-To-The-Home network management platform for provisioning, monitoring, and maintaining FTTH infrastructure.</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/React%2019-61DAFB?style=flat-square&logo=react&logoColor=black"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white"/>
  <img src="https://img.shields.io/badge/Tailwind%20CSS-06B6D4?style=flat-square&logo=tailwindcss&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Vite-646CFF?style=flat-square&logo=vite&logoColor=white"/>
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)

---

## 🔭 Overview

The **FTTH Management System** is an enterprise-grade platform designed to manage the complete lifecycle of Fiber-To-The-Home network operations. It enables telecom operators to efficiently handle customer provisioning, network inventory, capacity planning, billing, and maintenance — all from a single unified dashboard.

```
┌─────────────────────────────────────────────────────────────────┐
│                    FTTH MANAGEMENT SYSTEM                        │
├─────────────┬─────────────┬──────────────┬─────────────────────┤
│  Dashboard  │  Inventory  │  Customers   │    Maintenance      │
│  & Reports  │  Management │  & Billing   │    & Capacity       │
└──────┬──────┴──────┬──────┴──────┬───────┴──────────┬──────────┘
       │             │             │                   │
       └─────────────┴─────────────┴───────────────────┘
                           │
                    ┌──────┴──────┐
                    │  REST API   │
                    │ Spring Boot │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │    MySQL    │
                    │  Database   │
                    └─────────────┘
```

---

## 🏗 Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React + TS)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ │
│  │Dashboard │ │Inventory │ │Customers │ │  Plans   │ │ Users  │ │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └───┬────┘ │
│       └─────────────┴────────────┴─────────────┴───────────┘      │
│                              │ HTTP/REST                           │
└──────────────────────────────┼────────────────────────────────────┘
                               ▼
┌──────────────────────────────┼────────────────────────────────────┐
│                      BACKEND (Spring Boot)                         │
│  ┌────────────┐  ┌──────────────┐  ┌───────────────────────────┐ │
│  │ Controllers│──│   Services   │──│      Repositories         │ │
│  └────────────┘  └──────────────┘  └─────────────┬─────────────┘ │
│                                                   │               │
└───────────────────────────────────────────────────┼───────────────┘
                                                    ▼
                                          ┌─────────────────┐
                                          │   MySQL (3306)   │
                                          └─────────────────┘
```

---

## ✨ Features

| Module | Capabilities |
|--------|-------------|
| 📊 **Dashboard** | Real-time network overview, KPIs, connection stats |
| 📦 **Inventory** | Manage OLTs, Splitters, Ports — add/remove/view details |
| 👥 **Customers** | Full customer lifecycle — onboarding, billing, plan changes, disconnection, relocation |
| 🔗 **Connections** | Provision and manage fiber connections end-to-end |
| 📈 **Capacity** | Monitor port utilization and plan network expansion |
| 🛠 **Maintenance** | Schedule and track maintenance activities |
| 💰 **Plans** | Create and manage service plans and pricing |
| 👤 **User Management** | Role-based access (Admin, CSR) with CRUD operations |
| 🔐 **Authentication** | Secure login with role-based authorization |

---

## 🛠 Tech Stack

### Frontend
| Technology | Purpose |
|-----------|---------|
| React 19 | UI Framework |
| TypeScript | Type Safety |
| Tailwind CSS 4 | Styling |
| Vite 8 | Build Tool |
| React Router 7 | Navigation |

### Backend
| Technology | Purpose |
|-----------|---------|
| Java / Spring Boot | REST API |
| Spring Data JPA | ORM |
| Hibernate | Database Mapping |
| MySQL 8 | Relational Database |
| Maven | Dependency Management |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **MySQL 8+**
- **Maven 3.8+**

### 1️⃣ Database Setup

```sql
-- Create the database
CREATE DATABASE testdb;

-- Run the seed script
SOURCE sql/seed.sql;
```

### 2️⃣ Backend

```bash
cd ftth-Backend

# Configure database credentials in:
# src/main/resources/application.properties

# Build & Run
./mvnw spring-boot:run
```

> Backend runs on `http://localhost:8080`

### 3️⃣ Frontend

```bash
cd ftth-frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

> Frontend runs on `http://localhost:5173`

---

## 📁 Project Structure

```
FTTHProject/
├── ftth-Backend/                    # Spring Boot REST API
│   ├── src/main/java/ftth/
│   │   ├── controller/             # REST Controllers
│   │   │   ├── AdminController
│   │   │   ├── CSRController
│   │   │   ├── CustomerController
│   │   │   ├── InventoryController
│   │   │   ├── MaintController
│   │   │   └── UserManagementController
│   │   ├── model/                  # JPA Entities
│   │   │   ├── Customer, Olt, Splitter, Port
│   │   │   ├── Plan, Bill, ServiceArea
│   │   │   └── User, Role, EmailLog
│   │   ├── repository/            # Data Access Layer
│   │   ├── service/               # Business Logic
│   │   ├── config/                # Security & App Config
│   │   └── util/                  # Utilities
│   └── sql/seed.sql               # Database seed script
│
├── ftth-frontend/                  # React + TypeScript SPA
│   ├── src/
│   │   ├── pages/
│   │   │   ├── Dashboard/         # Network overview
│   │   │   ├── Inventory/         # OLT & Splitter management
│   │   │   ├── CustomerScreen/    # Customer lifecycle
│   │   │   ├── Connections/       # Connection provisioning
│   │   │   ├── Capacity/          # Port capacity monitoring
│   │   │   ├── Maintenance/       # Maintenance scheduling
│   │   │   ├── Plans/             # Service plan management
│   │   │   ├── Users/             # User administration
│   │   │   └── Login/             # Authentication
│   │   ├── components/            # Reusable UI components
│   │   ├── services/              # API service layer
│   │   ├── context/               # React Context (state)
│   │   ├── types/                 # TypeScript interfaces
│   │   └── utils/                 # Helper functions
│   └── vite.config.ts
│
└── README.md
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | User authentication |
| `GET` | `/api/customers` | List all customers |
| `POST` | `/api/customers` | Create new customer |
| `GET` | `/api/inventory/olts` | List all OLTs |
| `POST` | `/api/inventory/olts` | Add new OLT |
| `GET` | `/api/inventory/splitters` | List splitters |
| `GET` | `/api/connections` | List connections |
| `POST` | `/api/connections` | Provision connection |
| `GET` | `/api/capacity` | Get capacity data |
| `GET` | `/api/plans` | List service plans |
| `GET` | `/api/users` | List system users |
| `POST` | `/api/users` | Create user |

---

## 📸 Screenshots

> _Add screenshots of your application here_

| Dashboard | Inventory | Customer Management |
|-----------|-----------|-------------------|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Inventory](docs/screenshots/inventory.png) | ![Customers](docs/screenshots/customers.png) |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is proprietary and developed for internal use.

---

<p align="center">
  <strong>Built with ❤️ for Fiber Network Operations</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Active%20Development-brightgreen?style=flat-square"/>
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square"/>
</p>
