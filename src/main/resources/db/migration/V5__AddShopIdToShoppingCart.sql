-- 添加 shop_id 字段
ALTER TABLE shopping_cart
    ADD COLUMN (SHOP_ID BIGINT);

DELETE
FROM shopping_cart
WHERE USER_ID = 1;

INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER)
VALUES (1, 1, 1, 100);
INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER)
VALUES (1, 4, 2, 200);
INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER)
VALUES (1, 5, 2, 300);