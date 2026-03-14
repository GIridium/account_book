-- Enable UUID extension if needed in future
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL, -- NULL for system defaults
    CONSTRAINT uq_category_name_user UNIQUE (name, user_id) -- Prevent duplicate category names for same user (NULL user_id treats as unique per name for system)
);

-- Shared Books Table
CREATE TABLE IF NOT EXISTS shared_books (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    invite_code VARCHAR(10) NOT NULL UNIQUE,
    creator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Shared Book Members Table
CREATE TABLE IF NOT EXISTS shared_book_members (
    id BIGSERIAL PRIMARY KEY,
    shared_book_id BIGINT NOT NULL REFERENCES shared_books(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'EDITOR', 'VIEWER')),
    joined_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_shared_book_member UNIQUE (shared_book_id, user_id)
);

-- Saving Goals Table
CREATE TABLE IF NOT EXISTS saving_goals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    target_amount NUMERIC(12, 2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    deadline DATE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Bills (Transactions) Table
CREATE TABLE IF NOT EXISTS bills (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(12, 2) NOT NULL, -- Positive for expense, could be negative for income if designed that way, or use type
    category_id BIGINT NOT NULL REFERENCES categories(id),
    bill_date DATE NOT NULL,
    remark VARCHAR(255),
    merchant VARCHAR(100),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shared_book_id BIGINT REFERENCES shared_books(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_bills_user_id ON bills(user_id);
CREATE INDEX IF NOT EXISTS idx_bills_date ON bills(bill_date);
CREATE INDEX IF NOT EXISTS idx_bills_category ON bills(category_id);
CREATE INDEX IF NOT EXISTS idx_bills_shared_book ON bills(shared_book_id);

-- Initial Data: Default Categories (Idempotent insert)
INSERT INTO categories (name, icon, user_id)
SELECT '餐饮', 'food', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '餐饮' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '交通', 'transport', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '交通' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '购物', 'shopping', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '购物' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '娱乐', 'entertainment', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '娱乐' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '学习', 'study', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '学习' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '住房', 'housing', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '住房' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '医疗', 'medical', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '医疗' AND user_id IS NULL);
INSERT INTO categories (name, icon, user_id)
SELECT '其他', 'other', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '其他' AND user_id IS NULL);
