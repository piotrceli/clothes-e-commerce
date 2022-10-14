use clothes_e_commerce;

INSERT INTO role (name)
VALUES ('ADMIN'), ('USER');

INSERT INTO cart (total_value)
VALUES(0.00), (0.00), (0.00), (0.00);

INSERT INTO address (apartment_number, street, city, country)
VALUES (100, 'Górczewska', 'Warsaw', 'Poland'),
(17, 'Marsa', 'Warsaw', 'Poland'),
(33, 'Dworcowa', 'Płock', 'Poland'),
(61, 'Kartuska', 'Gdańsk', 'Poland');

INSERT INTO app_user (email, password, enabled, first_name, last_name, phone_number, date_of_birth, address_id, cart_id)
VALUES ('admin@email.com', '$2a$10$RCisbeq2PYJyQ6NKD17GK.SG3WsxvGUYIvn4YefKefeW/BThBwb6S', 1, 'admin', 'admin', '100100100', '1990-01-01', 1, 1),
('user@email.com', '$2a$10$RwEPbKlCRKX.A8z6hMS8tO/WXVudbV892yDZlcNE/L8IvQW/rulMC', 1, 'user', 'user', '200200200', '1980-02-02', 2, 2),
('tom@email.com', '$2a$10$YlPmm2A.XsutY/m2XYuE.eEIPBlVBDyJID4An9oUkcuU0lP3LPkR2', 1, 'tom', 'smith', '300300300', '1985-03-03', 3, 3),
('anna@email.com', '$2a$10$n9JpFw2WgIqqj1/IU0m70eD2ITe1f9XqIQ2E40EJdKNz0qb5Aj5JC', 1, 'anna', 'jones', '400400400', '1987-04-11', 4, 4);

INSERT INTO user_role 
VALUES (1,1), (2,2), (3,2), (4,2);

INSERT INTO category (name, weather_season)
VALUES('dresses', 0), ('t-shirts', 1), ('blouses', 2), ('jackets', 3), ('trousers', 4), ('hats', 0), ('scarfs', 3), ('underwear', 0);

INSERT INTO product (name, price, image_url, description)
VALUES ('summer dress - green', 199.99, '1.png', 'Dress for the long summer days'),
('summer dress - aqua', 199.99, '2.png', 'Dress for the long summer days'),
('tie belt dress', '219.99', '3.png', 'Casual dress'),
('casual dress', '159.99', '4.png', 'Brown casual dress'),
('basic white t-shirt', '39.99', '5.png', 'White t-shirt'),
('basic black t-shirt', '39.99', '6.png', 'Black t-shirt'),
('basic red t-shirt', '39.99', '7.png', 'Red t-shirt'),
('Sun t-shirt', '49.99', '8.png', 'T-shirt with logo'),
('Mountains t-shirt', '49.99', '9.png', 'T-shirt with logo'),
('Cat t-shirt', '69.99', '10.png', 'T-shirt with cat logo'),
('Black jumper', '89.99', '11.png', 'Casual jumper'),
('White jumper', '89.99', '12.png', 'Casual jumper'),
('Waist coat', '119.99', '13.png', 'slim waistcoat'),
('Leather jacket', '399.99', '14.png', 'bio leather jacket'),
('Bomber jacket', '129.99', '15.png', 'green bomber jacket'),
('Black fedora', '69.99', '16.png', 'black fedora hat'),
('Baker boy hat', '110.99', '17.png', 'casual leather hat'),
('Dappled scarf', '79.99', '18.png', 'warm scarf'),
('Black scarf', '89.99', '19.png', 'black warm scarf'),
('White socks', '19.99', '20.png', 'white woolen socks'),
('Black socks', '19.99', '21.png', 'Black woolen socks');

INSERT INTO product_category
VALUES (1,1), (2,1), (3,1), (4,1), (5,2), (6,2), (7,2), (8,2), (9,2), (10,2), (11,3), (12,3), (13,4), (14,4), (15,4), (16,5), (17,5), (18,6), (19,6), (20,7), (21,7);

INSERT INTO item (size, quantity, product_id)
VALUES('S',10,1), ('M',10,1), ('L',15,1),
('S',15,2), ('M',30,2), ('L',33,2),
('S',13,3), ('M',11,3), ('L',2,3), 
('XS',21,4), ('M',18,4), ('XL',44,4),
('M',13,5), ('L',1,5), ('XL',65,5),
('M',7,6), ('L',1,6), ('XL',65,6),
('M',12,7), ('L',31,7), ('XL',65,7),
('M',3,8), ('L',12,8), ('XL',44,8),
('M',3,9), ('L',11,9), ('XL',32,9),
('M',1,10), ('L',51,10), ('XL',21,10),
('M',31,11), ('L',2,11),
('M',11,12), ('L',12,12),
('L',112,13), ('XL',14,13),
('M',9,14), ('XL',1,14),
('M',6,15), ('XL',3,15),
('L',3,16),
('L',5,17),
('L',37,18),
('S',11,19),
('S',43,20), ('M',55,20), ('L',89,20),
('S',80,21), ('M',41,21), ('L',33,21);

INSERT INTO purchase_order (total_value, date_of_order, app_user_id)
VALUES (279.97, '2020-08-11 12:37', 2),
(49.99, '2020-10-21 14:15', 2),
(329.95, '2020-06-30 21:12', 3);

INSERT INTO order_item (amount, item_id, purchase_order_id)
VALUES (1, 2, 1), (2, 14, 1),
(1, 26, 2),
(3, 50, 3), (1,32, 3), (1, 1, 3);


















