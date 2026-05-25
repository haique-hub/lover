CREATE DATABASE IF NOT EXISTS lovers_space DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE lovers_space;

DROP TABLE IF EXISTS photo_comment;
DROP TABLE IF EXISTS photo;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS anniversary;
DROP TABLE IF EXISTS couple_relation;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    avatar_content_type VARCHAR(100) DEFAULT NULL COMMENT '头像MIME类型',
    avatar_data LONGBLOB COMMENT '头像二进制数据',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    signature VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE couple_relation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '发起用户ID',
    partner_id BIGINT DEFAULT NULL COMMENT '绑定对象用户ID',
    bind_code VARCHAR(32) DEFAULT NULL COMMENT '绑定码',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0待绑定 1已绑定 2已解绑',
    bind_time DATETIME DEFAULT NULL COMMENT '绑定时间',
    unbind_time DATETIME DEFAULT NULL COMMENT '解绑时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_couple_bind_code (bind_code),
    KEY idx_couple_user_id (user_id),
    KEY idx_couple_partner_id (partner_id),
    CONSTRAINT fk_couple_user FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_couple_partner FOREIGN KEY (partner_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情侣关系表';

CREATE TABLE album (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '创建人ID',
    name VARCHAR(100) NOT NULL COMMENT '相册名称',
    cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    description VARCHAR(255) DEFAULT NULL COMMENT '相册描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    KEY idx_album_user_id (user_id),
    CONSTRAINT fk_album_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

CREATE TABLE anniversary (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '创建人ID',
    title VARCHAR(100) NOT NULL COMMENT '纪念日名称',
    type VARCHAR(32) NOT NULL COMMENT '纪念日类型 LOVE_DAY/BIRTHDAY/MEMORY_DAY/CUSTOM',
    calendar_type VARCHAR(16) NOT NULL DEFAULT 'SOLAR' COMMENT '日期类型 SOLAR/LUNAR',
    anniversary_date DATE NOT NULL COMMENT '纪念日日期',
    lunar_year INT DEFAULT NULL COMMENT '农历年',
    lunar_month INT DEFAULT NULL COMMENT '农历月',
    lunar_day INT DEFAULT NULL COMMENT '农历日',
    lunar_leap_month TINYINT(1) DEFAULT NULL COMMENT '是否闰月',
    relation_scope VARCHAR(32) NOT NULL COMMENT '纪念日归属 SELF/PARTNER/BOTH',
    repeat_type VARCHAR(32) NOT NULL DEFAULT 'YEARLY' COMMENT '重复方式 ONCE/YEARLY',
    reminder_days INT DEFAULT NULL COMMENT '提前提醒天数',
    cover_photo_id BIGINT DEFAULT NULL COMMENT '封面照片ID',
    description VARCHAR(255) DEFAULT NULL COMMENT '纪念日说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    KEY idx_anniversary_user_id (user_id),
    KEY idx_anniversary_date (anniversary_date),
    CONSTRAINT fk_anniversary_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纪念日表';

CREATE TABLE photo (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    album_id BIGINT NOT NULL COMMENT '相册ID',
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件访问路径',
    content_type VARCHAR(100) DEFAULT NULL COMMENT '文件MIME类型',
    file_data LONGBLOB COMMENT '图片二进制数据',
    description VARCHAR(255) DEFAULT NULL COMMENT '照片描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    KEY idx_photo_album_id (album_id),
    KEY idx_photo_user_id (user_id),
    CONSTRAINT fk_photo_album FOREIGN KEY (album_id) REFERENCES album (id),
    CONSTRAINT fk_photo_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相片表';

CREATE TABLE photo_comment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    photo_id BIGINT NOT NULL COMMENT '照片ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    reply_user_id BIGINT DEFAULT NULL COMMENT '被回复用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID 0表示顶级评论',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    KEY idx_comment_photo_id (photo_id),
    KEY idx_comment_user_id (user_id),
    KEY idx_comment_parent_id (parent_id),
    CONSTRAINT fk_comment_photo FOREIGN KEY (photo_id) REFERENCES photo (id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_comment_reply_user FOREIGN KEY (reply_user_id) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片评论表';
