-- Add type column to categories
ALTER TABLE categories ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'EXPENSE';

-- Drop old constraint and add new one including type
ALTER TABLE categories DROP CONSTRAINT uq_category_name_user;
ALTER TABLE categories ADD CONSTRAINT uq_category_name_type_user UNIQUE (name, type, user_id);

-- Insert Income Categories
INSERT INTO categories (name, icon, type, user_id) VALUES ('工资收入', 'salary', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('加班收入', 'overtime', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('奖金收入', 'bonus', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('兼职收入', 'part_time', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('经营所得', 'business', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('投资收入', 'investment', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('礼金收入', 'gift', 'INCOME', NULL);
INSERT INTO categories (name, icon, type, user_id) VALUES ('其他', 'other_income', 'INCOME', NULL);

