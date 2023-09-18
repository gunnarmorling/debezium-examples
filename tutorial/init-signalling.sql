USE inventory;
CREATE TABLE debezium_signal (id VARCHAR(42) PRIMARY KEY, type VARCHAR(32) NOT NULL, data VARCHAR(2048) NULL);
GRANT INSERT  ON inventory.debezium_signal TO 'debezium';