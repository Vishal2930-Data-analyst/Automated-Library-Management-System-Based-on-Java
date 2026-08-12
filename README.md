# 📚 Automated Library Management System

> **A desktop-based library automation application built with Java Swing, JDBC, and MySQL to digitize and streamline library operations, from user authentication and book management to issue/return transactions and automated reminders.**

---

## 📌 Project Overview

The **Automated Library Management System** is a Java-based desktop application designed to replace traditional manual library management processes with a centralized digital system.

The application provides dedicated modules for managing **books, library members, authentication, issue/return transactions, entry records, and reminders** through an intuitive graphical user interface.

The system uses **Java Swing** for the frontend, **JDBC** for database connectivity, and **MySQL** for persistent data storage.

---

## 🎯 Objectives

* Digitize traditional library management operations.
* Reduce manual record-keeping and paperwork.
* Provide centralized management of books and members.
* Automate book issue and return transactions.
* Maintain accurate transaction records.
* Improve accessibility and usability through a graphical interface.
* Provide automated reminders for library activities.
* Secure the system through user authentication.
* Provide a structured database-driven solution for library operations.

---

## ✨ Key Features

### 🔐 User Authentication

* User login system
* New user registration
* Session management
* Secure access to the application

### 📊 Dashboard

* Centralized application dashboard
* Quick access to major library modules
* Library activity overview
* User-friendly navigation

### 📚 Book Management

* Add new books
* View available books
* Maintain book information
* Track book availability
* Search and manage library inventory

### 👥 Member Management

* Add library members
* View member records
* Maintain member information
* Manage registered library users

### 🔄 Book Transactions

* Issue books to members
* Return issued books
* Maintain issued-book records
* Track transaction history
* Maintain entry/transaction records

### 📧 Automated Notifications

* Email-based notification functionality
* Automated reminder service
* Reminder support for library activities

### 🎨 User Interface

* Java Swing-based graphical interface
* Structured navigation
* Custom application themes
* Dark mode support
* Modular UI design

---

## 🏗️ System Architecture

```text
                    ┌─────────────────────────┐
                    │      Java Swing UI      │
                    │                         │
                    │ Login | Dashboard       │
                    │ Books | Members         │
                    │ Issue | Return          │
                    │ Transactions            │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │     Application Logic   │
                    │                         │
                    │ Session Management      │
                    │ Theme Management        │
                    │ Reminder Service        │
                    │ Email Service            │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │          JDBC           │
                    │ Java Database Connectivity│
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │         MySQL            │
                    │     Database Layer       │
                    │                         │
                    │ Books | Members         │
                    │ Users | Transactions    │
                    └─────────────────────────┘
```

---

## 🛠️ Technology Stack

| Technology               | Purpose                           |
| ------------------------ | --------------------------------- |
| **Java**                 | Core application development      |
| **Java Swing**           | Graphical User Interface          |
| **JDBC**                 | Database connectivity             |
| **MySQL**                | Data storage and management       |
| **Eclipse IDE**          | Development environment           |
| **SMTP / Email Service** | Email notifications and reminders |

---

## 📂 Project Structure

```text
Automated-Library-Management-System-Java/
│
├── src/
│   │
│   ├── db/
│   │   └── DBConnection.java
│   │
│   ├── gui/
│   │   ├── AddBookUI.java
│   │   ├── AddMemberUI.java
│   │   ├── DashboardUI.java
│   │   ├── EntryRegisterUI.java
│   │   ├── IssueBookUI.java
│   │   ├── IssuedBooksUI.java
│   │   ├── LoginUI.java
│   │   ├── ReturnBookUI.java
│   │   ├── SignUpUI.java
│   │   ├── TransactionsUI.java
│   │   ├── ViewBooksUI.java
│   │   └── ViewMemberUI.java
│   │
│   ├── style/
│   │   ├── DarkModeToggle.java
│   │   ├── Theme.java
│   │   └── ThemeManager.java
│   │
│   └── util/
│       ├── EmailSender.java
│       ├── ReminderService.java
│       └── UserSession.java
│
├── database/
│   └── library_management.sql
│
├── screenshots/
│
├── .gitignore
└── README.md
```

---

## 🗄️ Database

The application uses **MySQL** as its relational database.

The database script is available at:

```text
database/library_management.sql
```

### Database Responsibilities

The database stores and manages:

* User accounts
* Book information
* Member information
* Issue records
* Return records
* Transaction history
* Library-related records

### Database Setup

1. Install MySQL Server and MySQL Workbench.
2. Create a database for the application.
3. Import:

```text
database/library_management.sql
```

4. Update the database connection configuration in:

```text
src/db/DBConnection.java
```

5. Ensure the MySQL JDBC Connector is available in the project.

---

## ⚙️ Installation & Setup

### Prerequisites

Before running the project, install:

* Java JDK 11 or compatible version
* Eclipse IDE
* MySQL Server
* MySQL Workbench
* MySQL Connector/J

### Setup Steps

#### 1. Clone the repository

```bash
git clone https://github.com/YOUR-USERNAME/Automated-Library-Management-System-Java.git
```

#### 2. Open in Eclipse

Import the project into Eclipse as an existing Java project.

#### 3. Configure MySQL

Import the SQL file:

```text
database/library_management.sql
```

#### 4. Configure Database Connection

Update the database URL, username, and password in:

```text
src/db/DBConnection.java
```

> **Security Note:** Never commit real database passwords, email passwords, API keys, or other credentials to the repository.

#### 5. Add JDBC Driver

Add the appropriate **MySQL Connector/J** library to the project build path.

#### 6. Run the Application

Run the application's main/login class from Eclipse.

---

## 🖥️ Application Screenshots

Screenshots of the application's major modules will be added below.

### 🔐 Login

*Add login screenshot here*

### 📊 Dashboard

*Add dashboard screenshot here*

### 📚 Book Management

*Add books screenshot here*

### 👥 Member Management

*Add members screenshot here*

### 📖 Issue Book

*Add issue-book screenshot here*

### ↩️ Return Book

*Add return-book screenshot here*

### 📋 Transactions

*Add transactions screenshot here*

---

## 🔄 Application Workflow

```text
        Start
          │
          ▼
     User Login
          │
          ▼
      Dashboard
          │
    ┌─────┼───────────────┐
    ▼     ▼               ▼
  Books Members      Transactions
    │     │               │
    ▼     ▼          ┌────┴────┐
  Manage Manage      ▼         ▼
  Books  Members    Issue     Return
                       │         │
                       └────┬────┘
                            ▼
                    Database Update
                            │
                            ▼
                     Email / Reminder
                            │
                            ▼
                           End
```

---

## 🔒 Security Considerations

The project follows basic security practices such as:

* Authentication before accessing the application.
* Session management using `UserSession`.
* Separation of database connectivity and UI components.
* Sensitive credentials should be maintained outside publicly committed source code.
* `.gitignore` is used to prevent local configuration files and generated files from being committed.

> For production deployment, additional security measures such as password hashing, environment variables, prepared statements throughout the application, and role-based authorization should be implemented.

---

## 🚀 Future Enhancements

Potential improvements include:

* Role-based access control for administrators and librarians.
* Advanced search and filtering.
* Automatic fine calculation.
* PDF and Excel report generation.
* Advanced dashboard analytics.
* Book reservation functionality.
* Barcode/QR-code based book identification.
* Cloud database integration.
* REST API integration.
* Backup and restore functionality.
* Improved notification and reminder scheduling.
* Enhanced password security and account recovery.

---

## 📈 Learning Outcomes

This project provided practical experience in:

* Java application development
* Object-Oriented Programming
* Java Swing GUI development
* Event-driven programming
* JDBC database connectivity
* MySQL database management
* CRUD operations
* Authentication and session management
* Application modularization
* Exception handling
* Email integration
* Git and GitHub version control

---

## 👨‍💻 Author

**Vishal Borse**

**MCA Student | Aspiring Data Analyst**

---

## ⭐ Project Highlights

**Java Swing** • **JDBC** • **MySQL** • **CRUD Operations** • **Authentication** • **Database Management** • **Email Automation** • **Dark Mode** • **Desktop Application**

---

## 📄 License

This project is available for educational and portfolio purposes.
