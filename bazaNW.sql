CREATE DATABASE IF NOT EXISTS forum_db;
USE forum_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    passwd_hash VARCHAR(255) NOT NULL,
    mail VARCHAR(255) NOT NULL UNIQUE,
    is_moderator BOOLEAN DEFAULT FALSE
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_toxic SMALLINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE deleted_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    moderated_by BIGINT NOT NULL,
    deleted_at TIMESTAMP NOT NULL,
    reason TEXT NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES comments(id),
    FOREIGN KEY (moderated_by) REFERENCES users(id)
);

CREATE TABLE mod_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moderator_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    action_details TEXT DEFAULT NULL,
    action_time TIMESTAMP NOT NULL,
    FOREIGN KEY (moderator_id) REFERENCES users(id),
    FOREIGN KEY (comment_id) REFERENCES comments(id)
);

ALTER TABLE comments
DROP FOREIGN KEY comments_ibfk_2;

ALTER TABLE comments
ADD CONSTRAINT comments_ibfk_2
FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE;

ALTER TABLE deleted_comments
DROP FOREIGN KEY deleted_comments_ibfk_1;

ALTER TABLE deleted_comments
ADD CONSTRAINT deleted_comments_ibfk_1
FOREIGN KEY (comment_id) REFERENCES comments(id)
ON DELETE CASCADE;

SELECT * from users;

SELECT * from posts;

SELECT * from comments;

SELECT * from mod_logs;

SHOW CREATE TABLE comments;
