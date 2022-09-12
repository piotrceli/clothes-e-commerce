CREATE DATABASE IF NOT EXISTS `clothes_e_commerce`;
USE `clothes_e_commerce`;

CREATE TABLE `product` (
  `id` bigint AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `price` double(10,2) NOT NULL,
  `image_url` varchar(255),
  `description` varchar(255) NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `category` (
  `id` bigint AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `weather_season` varchar(50) NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `item` (
  `id` bigint AUTO_INCREMENT,
  `size` varchar(50) NOT NULL,
  `quantity` int NOT NULL,
  `product_id` bigint,

  CONSTRAINT `FK_PRODUCT_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `product_category` (
  `product_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  
  PRIMARY KEY (`product_id`,`category_id`),
  CONSTRAINT `FK_PRODUCT_0` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_CATEGORY_0` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `address` (
  `id` bigint AUTO_INCREMENT,
  `apartment_number` int NOT NULL,
  `street` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `country` varchar(50) NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `cart` (
  `id` bigint AUTO_INCREMENT,
  `total_value` decimal(10,2) NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `cart_item` (
  `id` bigint AUTO_INCREMENT,
  `amount` int NOT NULL,
  `item_id` bigint NOT NULL,
  `cart_id` bigint,

  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ITEM_0` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_CART_0` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `app_user` (
  `id` bigint AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `password` varchar(68) NOT NULL,
  `enabled` tinyint NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `date_of_birth` date NOT NULL,
  `address_id` bigint,
  `cart_id` bigint,
  
  CONSTRAINT `FK_ADDRESS_0` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FK_CART_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `role` (
  `id` bigint AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

INSERT INTO `role`
VALUES 
(1, 'ADMIN'),
(2, 'USER');

CREATE TABLE `user_role` (
  `app_user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  
  PRIMARY KEY (`app_user_id`,`role_id`),
  CONSTRAINT `FK_APP_USER_0` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK_ROLE_0` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `purchase_order` (
  `id` bigint AUTO_INCREMENT,
  `total_value` decimal(10,2) NOT NULL,
  `date_of_order` datetime NOT NULL,
  `app_user_id` bigint,
  
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_APP_USER_1` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `order_item` (
  `id` bigint AUTO_INCREMENT,
  `amount` int NOT NULL,
  `item_id` bigint NOT NULL,
  `purchase_order_id` bigint,

  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ITEM_1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ORDER_0` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;


