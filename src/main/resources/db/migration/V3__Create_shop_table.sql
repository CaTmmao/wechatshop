CREATE TABLE shop
(
    ID            BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME          VARCHAR(100),
    DESCRIPTION   VARCHAR(1024),
    IMG_URL       VARCHAR(1024),
    OWNER_USER_ID BIGINT,
    STATUS        VARCHAR(16),
    CREATED_AT    TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT    TIMESTAMP ON UPDATE NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO shop (ID, NAME, DESCRIPTION, IMG_URL, OWNER_USER_ID, STATUS)
VALUES (1, 'shop1', 'desc1', 'url1', 1, 'ok');
INSERT INTO shop (ID, NAME, DESCRIPTION, IMG_URL, OWNER_USER_ID, STATUS)
VALUES (2, 'shop2', 'desc2', 'url2', 1, 'ok');