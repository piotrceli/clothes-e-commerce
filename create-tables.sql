CREATE DATABASE IF NOT EXISTS clothes_e_commerce;
USE clothes_e_commerce;

CREATE TABLE product (
  id bigint AUTO_INCREMENT,
  product_name varchar(50) NOT NULL,
  price double(10,2) NOT NULL,
  image_url varchar(255),
  product_description text NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE category (
  id bigint AUTO_INCREMENT,
  category_name varchar(50) NOT NULL,
  weather_season int NOT NULL,

  CONSTRAINT UQ_category_category_name UNIQUE (category_name),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE item (
  id bigint AUTO_INCREMENT,
  size varchar(10) NOT NULL,
  quantity int NOT NULL,
  product_id bigint,

  CONSTRAINT FK_item_product FOREIGN KEY (product_id) REFERENCES product (id),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE product_category (
  product_id bigint NOT NULL,
  category_id bigint NOT NULL,
  
  PRIMARY KEY (product_id, category_id),
  CONSTRAINT FK_product_category_product FOREIGN KEY (product_id) REFERENCES product (id),
  CONSTRAINT FK_product_category_category FOREIGN KEY (category_id) REFERENCES category (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE address (
  id bigint AUTO_INCREMENT,
  apartment_number int NOT NULL,
  street varchar(50) NOT NULL,
  city varchar(50) NOT NULL,
  country varchar(50) NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE cart (
  id bigint AUTO_INCREMENT,
  total_value decimal(10,2) NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE cart_item (
  id bigint AUTO_INCREMENT,
  amount int NOT NULL,
  item_id bigint NOT NULL,
  cart_id bigint,

  PRIMARY KEY (id),
  CONSTRAINT FK_cart_item_item FOREIGN KEY (item_id) REFERENCES item (id),
  CONSTRAINT FK_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE app_user (
  id bigint AUTO_INCREMENT,
  email varchar(50) NOT NULL,
  user_password varchar(68) NOT NULL,
  enabled tinyint NOT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  phone_number varchar(20) NOT NULL,
  date_of_birth date NOT NULL,
  address_id bigint,
  cart_id bigint,
  
  CONSTRAINT FK_app_user_address FOREIGN KEY (address_id) REFERENCES address (id),
  CONSTRAINT FK_app_user_cart FOREIGN KEY (cart_id) REFERENCES cart (id),
  CONSTRAINT UQ_app_user_address_id UNIQUE (address_id),
  CONSTRAINT UQ_app_user_cart_id UNIQUE (cart_id),
  CONSTRAINT UQ_app_user_email UNIQUE (email),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE app_role (
  id bigint AUTO_INCREMENT,
  role_name varchar(50) NOT NULL,
  
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE user_role (
  app_user_id bigint NOT NULL,
  app_role_id bigint NOT NULL,
  
  PRIMARY KEY (app_user_id, app_role_id),
  CONSTRAINT FK_user_role_app_user FOREIGN KEY (app_user_id) REFERENCES app_user (id),
  CONSTRAINT FK_user_role_app_role FOREIGN KEY (app_role_id) REFERENCES app_role (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE purchase_order (
  id bigint AUTO_INCREMENT,
  total_value decimal(10,2) NOT NULL,
  date_of_order datetime NOT NULL,
  app_user_id bigint,
  
  PRIMARY KEY (id),
  CONSTRAINT FK_purchase_order_app_user FOREIGN KEY (app_user_id) REFERENCES app_user (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE order_item (
  id bigint AUTO_INCREMENT,
  amount int NOT NULL,
  item_id bigint NOT NULL,
  purchase_order_id bigint,

  PRIMARY KEY (id),
  CONSTRAINT FK_order_item_item FOREIGN KEY (item_id) REFERENCES item (id),
  CONSTRAINT FK_order_item_purchase_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;


