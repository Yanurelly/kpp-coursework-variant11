DROP DATABASE IF EXISTS warehouse_replenishment;
CREATE DATABASE warehouse_replenishment;
USE warehouse_replenishment;

-- Таблиця постачальників
CREATE TABLE supplier (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(160) NOT NULL,
                          contact_info VARCHAR(255) NOT NULL,
                          delay_risk_score DOUBLE NOT NULL
);

-- Таблиця товарів
CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         sku VARCHAR(40) NOT NULL,
                         name VARCHAR(160) NOT NULL,
                         category VARCHAR(120) NOT NULL
);

-- Таблиця запасів (посилається на товар)
CREATE TABLE inventory (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           product_id BIGINT NOT NULL,
                           current_stock DOUBLE NOT NULL,
                           min_required_stock DOUBLE NOT NULL,
                           consumption_intensity DOUBLE NOT NULL,
                           FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Таблиця замовлень на поповнення (посилається на запаси та постачальника)
CREATE TABLE replenishment_order (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     inventory_id BIGINT NOT NULL,
                                     supplier_id BIGINT NOT NULL,
                                     state VARCHAR(30) NOT NULL,
                                     f1 DOUBLE NOT NULL,
                                     f2 DOUBLE NOT NULL,
                                     f3 DOUBLE NOT NULL,
                                     risk_index DOUBLE NOT NULL,
                                     FOREIGN KEY (inventory_id) REFERENCES inventory(id),
                                     FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

-- Початкові демонстраційні дані

INSERT INTO supplier (name, contact_info, delay_risk_score) VALUES
                                                                ('ТОВ "Склад-Плюс"', 'Полтава, вул. Складська 1', 15.0),
                                                                ('ПАТ "ПромПостач"', 'Київ, пр. Заводський 12', 5.0);

INSERT INTO product (sku, name, category) VALUES
                                              ('PRD-001', 'Ноутбук Acer Aspire 5', 'Електроніка'),
                                              ('PRD-002', 'Бездротові навушники Nokia', 'Електроніка');

INSERT INTO inventory (product_id, current_stock, min_required_stock, consumption_intensity) VALUES
                                                                                                 (1, 15.0, 20.0, 2.0),
                                                                                                 (2, 50.0, 100.0, 10.0);

INSERT INTO replenishment_order (inventory_id, supplier_id, state, f1, f2, f3, risk_index) VALUES
                                                                                               (1, 1, 'CREATED', 0.0, 0.0, 0.0, 0.0),
                                                                                               (2, 2, 'CREATED', 0.0, 0.0, 0.0, 0.0);