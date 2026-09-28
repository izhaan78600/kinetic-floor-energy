# Smart Urban Subway Kinetic Floor Energy Harvesting System

A complete web-based College Engineering & Management Project for monitoring, managing, and accounting for kinetic floor energy harvesting across urban subway/metro stations.

---

## 🚀 Technology Stack

- **Backend Framework**: Java 21 & Spring Boot 3.3.4
- **Persistence & ORM**: Spring Data JPA / Hibernate
- **Database**: MySQL Server 26.x / 8.x (`kinetic_energy_db`)
- **Security / Password Hashing**: Spring Security Crypto (BCrypt)
- **Frontend**: Responsive Single-Page Application (HTML5, Tailwind CSS, FontAwesome 6, Chart.js)
- **Currency**: Indian Rupees (INR ₹)
- **Architecture**: REST API Backend + Double-Entry Accounting Engine + Embedded Static Frontend

---

## 🗄️ Database Architecture (`kinetic_energy_db`)

All application records are permanently stored in MySQL. There is **no SQLite, no MongoDB, no localStorage database**.

### The 14 Required MySQL Tables:
1. `users`: Normal subway operators/users with BCrypt hashed passwords and roles.
2. `admin_users`: Separate table for metro administrators with BCrypt hashed passwords.
3. `power_grid_customers`: Utility companies and regional power grids buying harvested energy.
4. `tile_vendors`: Suppliers and manufacturers of piezoelectric & electromagnetic kinetic floor tiles.
5. `kinetic_tiles`: Catalog of kinetic tile models, power ratings, inventory, and specifications.
6. `station_sectors`: High-footfall subway zones (turnstiles, corridors, platforms, concourses).
7. `telemetry`: Daily logs of footstep counts and harvested energy (kWh).
8. `sales_orders`: Power purchase contracts and sales orders with power grids.
9. `customer_invoices`: Commercial energy sales invoices generated in INR.
10. `vendor_bills`: Invoices received from vendors for tile replacement, repairs, transducer overhaul, and inverter maintenance.
11. `payments`: Settlement records for customer receivables and vendor payables.
12. `budgets`: Annual planned revenue vs actual revenue, planned maintenance vs actual maintenance, and variances.
13. `chart_of_accounts`: Ledger accounts (Bank, Receivables, Payables, Revenue, Expenses, Capital Fund).
14. `journal_entries`: Double-entry accounting transactions tracking all Debits and Credits.

---

## ⚙️ MySQL Configuration

The application is configured in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kinetic_energy_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:root}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

You can customize credentials by setting environment variables `MYSQL_USER` and `MYSQL_PASSWORD`, or by modifying `application.properties`.

---

## 🔑 Default Login Credentials

On initial startup, `DataSeederService` automatically seeds initial master data, telemetry, and administrator credentials into MySQL:

| Role | Email | Password | Target Portal |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@metro.in` | `admin123` | **Admin Control Panel** (Full 12-tab management & accounting) |
| **Subway Operator / User** | `user@metro.in` | `user123` | **User Portal** (Real-time telemetry, burst simulation & status) |

Administrator accounts are strictly pre-configured for authorized personnel (`admin@metro.in` / `admin123`). Only **Admin Sign In** is supported. Normal users/operators can freely register using **User Sign Up** or sign in with `user@metro.in` / `user123`.

---

## 🏃 Running the Application

### Option 1: Using the Windows Batch Script
Double-click `run.bat` or run:
```cmd
run.bat
```

### Option 2: Using the PowerShell Script
```powershell
.\run.ps1
```

### Option 3: Using Maven Spring Boot Plugin
```bash
mvn spring-boot:run
```

### Option 4: Using the Packaged Executable JAR
```bash
mvn clean package -DskipTests
java -jar target/kinetic-energy-system-1.0.0.jar
```

Open your browser at:
👉 **`http://localhost:8080`**

---

## 📊 Double-Entry Accounting Engine

Every commercial transaction automatically creates balanced journal entries in `journal_entries`:

1. **Harvested Energy Sale**:
   - `Debit`: Customer Receivable (`Asset`)
   - `Credit`: Harvested Power Sales Revenue (`Revenue`)

2. **Customer Payment Received**:
   - `Debit`: Bank (`Asset`)
   - `Credit`: Customer Receivable (`Asset`)

3. **Vendor Bill (Repairs / Spares / Inverter Maintenance)**:
   - `Debit`: Maintenance Expense / Inverter Expenses (`Expense`)
   - `Credit`: Vendor Payable (`Liability`)

4. **Vendor Payment Settled**:
   - `Debit`: Vendor Payable (`Liability`)
   - `Credit`: Bank (`Asset`)

---

## 🔍 MySQL Verification Commands

Open a terminal or MySQL Workbench to verify stored records:

```sql
-- Connect to the database
USE kinetic_energy_db;

-- 1. View all 14 tables
SHOW TABLES;

-- 2. Verify registered users and admin users (passwords are BCrypt hashed)
SELECT id, name, email, role, created_at FROM users;
SELECT id, name, email, created_at FROM admin_users;

-- 3. Verify telemetry data
SELECT id, sector_id, date, step_count, energy_kwh FROM telemetry ORDER BY id DESC LIMIT 10;

-- 4. Verify customer invoices
SELECT id, sales_order_id, amount_inr, status, invoice_date FROM customer_invoices;

-- 5. Verify vendor bills
SELECT id, vendor_id, description, amount_inr, status, bill_date FROM vendor_bills;

-- 6. Verify payments
SELECT id, payment_type, amount_inr, payment_method, status FROM payments;

-- 7. Verify general ledger & journal entries
SELECT id, entry_date, debit_account, credit_account, amount_inr, journal_type FROM journal_entries ORDER BY id DESC;

-- 8. Verify chart of accounts balance (INR)
SELECT id, account_name, account_type, balance_inr FROM chart_of_accounts;
```

---

## 🎓 College Project Demo Walkthrough

1. **User Sign Up & Login**: Create a student/operator account or log in with `user@metro.in` / `user123`.
2. **User Portal**: View real-time energy harvesting stats and trigger live footsteps simulation.
3. **Admin Sign Up & Login**: Log in as Administrator using `admin@metro.in` / `admin123`.
4. **Admin Dashboard**: Inspect live KPI cards (Total Footsteps, Total kWh Generated, Power Sold, Total Revenue ₹, Maintenance Expenses ₹, Net Profit ₹) and interactive charts.
5. **Station Sectors**: Add or inspect station sectors (turnstiles, corridors, escalator exits).
6. **Kinetic Telemetry**: Enter new daily footstep telemetry and view energy generation calculations.
7. **Energy Sales Order & Invoice**: Create an energy contract (e.g. 1,000 kWh @ ₹8/kWh = ₹8,000) and verify that an invoice is generated.
8. **Receive Customer Payment**: Settle the invoice via Bank Transfer.
9. **Vendor Bills & Payments**: Record maintenance bills for transducer overhauls or tile replacements and settle payments.
10. **Budgeting**: Compare planned revenue vs actual energy revenue and analyze variances.
11. **Financial Reports**: Open Profit & Loss, Budget Report, Energy Report, and General Ledger. All figures display in **INR (₹)**.
12. **MySQL Check**: Run `SELECT * FROM journal_entries;` in MySQL to show the examiner live data persistence.
