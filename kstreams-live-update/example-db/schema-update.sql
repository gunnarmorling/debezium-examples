# Switch to this database
USE inventory;

DROP TABLE IF EXISTS orders;

CREATE TABLE categories (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  average_price BIGINT,
  PRIMARY KEY (id)
) AUTO_INCREMENT = 100001;

INSERT INTO categories VALUES (default, 'Toys', 4500);
INSERT INTO categories VALUES (default, 'Books', 2200);
INSERT INTO categories VALUES (default, 'Computers', 6700);
INSERT INTO categories VALUES (default, 'Tools', 4800);
INSERT INTO categories VALUES (default, 'Plants', 1900);
INSERT INTO categories VALUES (default, 'Food', 500);
INSERT INTO categories VALUES (default, 'Furniture', 2700);
INSERT INTO categories VALUES (default, 'Cloth', 3700);

CREATE TABLE orders (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ts TIMESTAMP NOT NULL,
  purchaser_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  category_id BIGINT NOT NULL,
  quantity INTEGER NOT NULL,
  sales_price BIGINT NOT NULL
) AUTO_INCREMENT = 100001;

ALTER TABLE orders ADD CONSTRAINT fk_orders_product_id FOREIGN KEY (product_id) REFERENCES inventory.products(id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_purchaser_id FOREIGN KEY (purchaser_id) REFERENCES inventory.customers(id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_category_id FOREIGN KEY (category_id) REFERENCES inventory.categories(id);

CREATE TABLE rules (
  id BIGINT NOT NULL AUTO_INCREMENT,
  description VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT = 101;

CREATE TABLE systems (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT = 1001;

CREATE TABLE reports (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_id BIGINT NOT NULL,
  system_id BIGINT NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT = 10001;

ALTER TABLE reports ADD CONSTRAINT fk_reports_rule_id FOREIGN KEY (rule_id) REFERENCES inventory.rules(id);
ALTER TABLE reports ADD CONSTRAINT fk_reports_system_id FOREIGN KEY (system_id) REFERENCES inventory.systems(id);

INSERT INTO rules VALUES (default, 'Something is wrong 1');
INSERT INTO rules VALUES (default, 'Something is wrong 2');

INSERT INTO systems VALUES (default, 'System A');
INSERT INTO systems VALUES (default, 'System B');

INSERT INTO reports VALUES (default, 101, 1001);
INSERT INTO reports VALUES (default, 102, 1001);
INSERT INTO reports VALUES (default, 101, 1002);
