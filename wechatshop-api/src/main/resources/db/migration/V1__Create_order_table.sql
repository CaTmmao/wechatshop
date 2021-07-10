CREATE TABLE `order`
(
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID         BIGINT,
    TOTAL_PRICE     BIGINT,      -- 价格，单位分
    ADDRESS         VARCHAR(1024),
    EXPRESS_COMPANY VARCHAR(16),
    EXPRESS_ID      VARCHAR(128),
    STATUS          VARCHAR(16), -- pending 待付款 paid 已付款 delivered 物流中 received 已收货
    CREATED_AT      TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT      TIMESTAMP ON UPDATE NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `order` (ID, USER_ID, TOTAL_PRICE, ADDRESS, EXPRESS_COMPANY, EXPRESS_ID, STATUS)
VALUES (1, 1, 1400, '火星', '顺丰', '运单1234567', 'delivered');