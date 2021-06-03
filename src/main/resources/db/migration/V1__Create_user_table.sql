CREATE TABLE `user` (
    id bigint primary key auto_increment,
    name varchar(100),
    tel varchar(100) unique,
    avatar_url varchar(1024),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
)