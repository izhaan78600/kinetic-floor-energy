-- ==============================================================================
-- SMART URBAN SUBWAY KINETIC FLOOR ENERGY HARVESTING SYSTEM
-- DATABASE SCHEMA DEFINITION FOR MYSQL
-- DATABASE NAME: kinetic_energy_db
-- CURRENCY: INDIAN RUPEES (INR ₹)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS kinetic_energy_db;
USE kinetic_energy_db;

-- 1. users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. admin_users table
CREATE TABLE IF NOT EXISTS admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. power_grid_customers table
CREATE TABLE IF NOT EXISTS power_grid_customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    contact VARCHAR(100),
    email VARCHAR(255),
    address TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. tile_vendors table
CREATE TABLE IF NOT EXISTS tile_vendors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_name VARCHAR(255) NOT NULL,
    contact VARCHAR(100),
    email VARCHAR(255),
    address TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. kinetic_tiles table
CREATE TABLE IF NOT EXISTS kinetic_tiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tile_name VARCHAR(255) NOT NULL,
    tile_type VARCHAR(100),
    price_inr DOUBLE NOT NULL DEFAULT 0.0,
    quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. station_sectors table
CREATE TABLE IF NOT EXISTS station_sectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(255) NOT NULL,
    sector_name VARCHAR(255) NOT NULL,
    location TEXT,
    tile_count INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. telemetry table
CREATE TABLE IF NOT EXISTS telemetry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sector_id BIGINT NOT NULL,
    date DATE NOT NULL,
    step_count BIGINT NOT NULL,
    energy_kwh DOUBLE NOT NULL,
    CONSTRAINT fk_telemetry_sector FOREIGN KEY (sector_id) REFERENCES station_sectors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. sales_orders table
CREATE TABLE IF NOT EXISTS sales_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    energy_kwh DOUBLE NOT NULL,
    price_per_kwh DOUBLE NOT NULL,
    total_amount_inr DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES power_grid_customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. customer_invoices table
CREATE TABLE IF NOT EXISTS customer_invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_order_id BIGINT NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,
    amount_inr DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Unpaid',
    CONSTRAINT fk_invoice_sales_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. vendor_bills table
CREATE TABLE IF NOT EXISTS vendor_bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id BIGINT NOT NULL,
    bill_date DATE NOT NULL,
    description TEXT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    amount_inr DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Unpaid',
    CONSTRAINT fk_vendor_bills_vendor FOREIGN KEY (vendor_id) REFERENCES tile_vendors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_type VARCHAR(50) NOT NULL, -- CUSTOMER, VENDOR
    reference_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount_inr DOUBLE NOT NULL,
    payment_method VARCHAR(100) NOT NULL DEFAULT 'Bank Transfer',
    status VARCHAR(50) NOT NULL DEFAULT 'Completed'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sector_id BIGINT NOT NULL,
    budget_year INT NOT NULL,
    planned_revenue_inr DOUBLE NOT NULL DEFAULT 0.0,
    planned_maintenance_inr DOUBLE NOT NULL DEFAULT 0.0,
    actual_revenue_inr DOUBLE NOT NULL DEFAULT 0.0,
    actual_maintenance_inr DOUBLE NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_budgets_sector FOREIGN KEY (sector_id) REFERENCES station_sectors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. chart_of_accounts table
CREATE TABLE IF NOT EXISTS chart_of_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_name VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(50) NOT NULL, -- Asset, Liability, Equity, Revenue, Expense
    balance_inr DOUBLE NOT NULL DEFAULT 0.0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. journal_entries table
CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journal_type VARCHAR(100) NOT NULL,
    reference_type VARCHAR(100),
    reference_id BIGINT,
    debit_account VARCHAR(255) NOT NULL,
    credit_account VARCHAR(255) NOT NULL,
    amount_inr DOUBLE NOT NULL,
    entry_date DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
