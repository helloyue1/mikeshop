CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
                                     username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，用于登录',
                                     email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱，用于登录和找回密码',
                                     password VARCHAR(255) NOT NULL COMMENT '密码哈希值，不存储明文密码',
                                     salt VARCHAR(50) NOT NULL COMMENT '密码盐值，增加安全性',
                                     nickname VARCHAR(50) COMMENT '用户昵称',
                                     status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常',
                                     INDEX idx_username (username),
                                     INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表，存储登录相关信息';


INSERT INTO users (username, email, password, salt, nickname)
VALUES
    ('testuser', 'test@example.com', '123456', 'random_salt_123', '测试用户');

CREATE TABLE `user_address` (
         `addressid` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
         `user_id` INT NOT NULL COMMENT '关联用户ID',
         `receiver_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
         `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收件人手机号',
         `province` VARCHAR(50) NOT NULL COMMENT '省份',
     `city` VARCHAR(50) NOT NULL COMMENT '城市',
         `district` VARCHAR(50) NOT NULL COMMENT '区/县',
         `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
         `postal_code` VARCHAR(20) DEFAULT NULL COMMENT '邮政编码',
         `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址：0=否，1=是',
         `create_time` DATETIME NOT NULL COMMENT '创建时间',  -- 不设置默认值，由程序维护
        `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
         PRIMARY KEY (`user_id`),
        KEY `idx_user_id` (`user_id`),
         CONSTRAINT `fk_user_address_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

CREATE TABLE `user_balance` (
                                `accout_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                `user_id` VARCHAR(64) NOT NULL COMMENT '用户唯一标识（如用户UID）',
                                `total_asset` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总资产（元）',
                                `last_updated` DATETIME NOT NULL COMMENT '最后更新时间',
                                PRIMARY KEY (`user_id`),
                                UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户余额总览';


CREATE TABLE `payment_method` (
                                  `pay_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
                                  `type` VARCHAR(200) NOT NULL COMMENT '支付方式类型：支付宝 银行卡 微信等',
                                  `account_info` VARCHAR(200) NOT NULL COMMENT '脱敏后的账户信息（如支付宝账号、银行卡尾号）',
                                  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认支付方式：1-是 0-否',
                                  `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`pay_id`),
                                  KEY `idx_user_id` (`user_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户支付方式绑定表';


CREATE TABLE `user_assets` (
                               `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                               `user_id` VARCHAR(64) NOT NULL COMMENT '用户唯一标识',
                               `asset_type` TINYINT NOT NULL COMMENT '资产类型：1=购物积分, 2=钻石, 3=优惠券',
                               `quantity` INT NOT NULL COMMENT '数量（积分点/钻石个/优惠券张）',
                               `value` DECIMAL(10,2) NOT NULL COMMENT '资产价值（元）',
                               PRIMARY KEY (`id`),
                               KEY `idx_user_asset` (`user_id`, `asset_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户虚拟资产明细';

-- 4. 交易记录表（可扩展支付方式、订单关联等）
CREATE TABLE `user_transactions` (
                                     `trans_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易ID',
                                     `user_id` VARCHAR(64) NOT NULL COMMENT '用户唯一标识',
                                     `trans_date` DATE NOT NULL COMMENT '交易日期',
                                     `trans_type` TINYINT NOT NULL COMMENT '交易类型：1=充值, 2=消费, 3=积分获取',
                                     `description` VARCHAR(255) NOT NULL COMMENT '交易描述（如“支付宝充值”“购买商品”）',
                                     `amount` DECIMAL(10,2) NOT NULL COMMENT '交易金额（+为收入，-为支出）',
                                     `status` TINYINT NOT NULL COMMENT '状态：1=成功, 2=失败（示例，可扩展）',
                                     PRIMARY KEY (`trans_id`),
                                     KEY `idx_user_date` (`user_id`, `trans_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户交易流水记录';


CREATE TABLE `user_category` (
                                 `category_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
                                 `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                                 `categorySon_name` VARCHAR(50) DEFAULT '' COMMENT '二级菜单',
                                 `product_count` INT NOT NULL COMMENT '商品数量',
                                 `status` VARCHAR(50) NOT NULL DEFAULT '启用' COMMENT '状态（禁用/启用）',
                                 `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                 PRIMARY KEY (`category_id`),
                                 KEY `idx_category_name` (`category_name`) COMMENT '分类名称索引，加速搜索',
                                 KEY `idx_categorySon_name` (`categorySon_name`) COMMENT '二级分类名称索引'
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商城分类表（支持二级分类）';


CREATE TABLE `product` (
                           `product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品主键ID',
                           `product_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
                           `category` VARCHAR(255) NOT NULL COMMENT '商品分类',
                           `price` Integer NOT NULL COMMENT '商品价格',
                           `stock` INT NOT NULL COMMENT '商品总库存（可结合 SKU 库存做汇总，或根据业务需求决定是否需要）',
                           `description` TEXT COMMENT '商品描述',
                            product_value   VARCHAR(255)  COMMENT '=商品规格属性',
                           `create_time` DATETIME NOT NULL COMMENT '创建时间',
                           `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`product_id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品主表';



CREATE TABLE `mall_image` (
                              `image_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID（主键）',
                              `image_url` VARCHAR(500) NOT NULL COMMENT '图片访问URL',
                              `image_name` VARCHAR(200) COMMENT '图片原始名称',
                              `file_size` INT COMMENT '图片大小（字节）',
                              `file_type` VARCHAR(50) COMMENT '图片类型（如image/png）',
                              `product_id` BIGINT COMMENT '关联业务ID（如商品ID）',
                              `create_time` DATETIME NOT NULL  COMMENT '上传时间',
                              `user_id` BIGINT COMMENT '上传人ID',
                              PRIMARY KEY (`image_id`),
                              KEY `idx_related` (`product_id`)COMMENT '按业务关联查询图片'
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT '商城图片表（存储图片URL及关联信息）';


CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY, -- 自增主键，唯一标识订单记录
                        user_id INT, -- 对应实体类 user_id，关联用户表
                        image VARCHAR(255), -- 对应实体类 image，存储商品照片地址
                        product_name VARCHAR(255), -- 对应实体类 product_name，商品名称
                        product_allPrice DECIMAL(10, 2), -- 商品总价，建议用 DECIMAL 存金额更精确，若需保持 String 类型可改 VARCHAR，这里按实际业务调整
                        product_price DECIMAL(10, 2), -- 商品单价，同理建议 DECIMAL
                        status VARCHAR(50), -- 订单状态，如待处理、已确认等
                        create_time DATETIME, -- 创建时间，建议用 DATETIME 类型存时间，方便后续时间相关操作，若需 String 可改 VARCHAR
                        order_address VARCHAR(255), -- 用户地址
                        order_userSay VARCHAR(255), -- 用户备注
                        pay_method VARCHAR(50) -- 支付方式
)ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;


CREATE TABLE `image` (
                         `image_id` INT NOT NULL AUTO_INCREMENT COMMENT '图片ID，自增主键',
                         `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL地址',
                         `image_category` VARCHAR(100) DEFAULT NULL COMMENT '图片分类',
                         `create_time` DATETIME  COMMENT '创建时间，默认当前时间',
                         PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片信息表，用于存储系统中的图片资源';

CREATE TABLE `collect` (
                           `collect_id` INT NOT NULL AUTO_INCREMENT COMMENT '收藏自增ID，主键',
                           `user_id` INT NOT NULL COMMENT '用户ID，关联用户表的主键',
                           `collect_productName` VARCHAR(255) NOT NULL COMMENT '收藏商品名称',
                           `collect_productPrice` VARCHAR(50) NOT NULL COMMENT '收藏商品价格（用字符串存储，避免浮点精度问题）',
                           `collect_productImg` VARCHAR(255) DEFAULT NULL COMMENT '收藏商品图片地址',
                           `collect_description` TEXT DEFAULT NULL COMMENT '收藏商品描述（长文本类型，支持较长描述）',
                           PRIMARY KEY (`collect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户商品收藏表';