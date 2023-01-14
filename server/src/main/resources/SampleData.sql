INSERT INTO USERS
(NAME, EMAIL)
VALUES ('Anton', 'agabov@gmail.com'),
       ('Dmitriy', 'dim2a@gmail.com'),
       ('Ivan', 'ivan@gmail.com'),
       ('Konstantin', 'kostya@gmail.com'),
       ('Olga', 'olga@gmail.com'),
       ('Petr', 'petya@gmail.com');


INSERT INTO ITEMS
(NAME, DESCRIPTION, IS_AVAILABLE, OWNER_ID)
VALUES ('Аккумуляторная дрель', 'Аккумуляторная дрель + аккумулятор', true, 1),
       ('лампочка', 'светодиодная лампа новая', true, 3),
       ('тапочки', 'теплые, домашние', true, 4),
       ('полотенце', 'махровое, синее', true, 2),
       ('компьютер', 'пентиум 4', true, 3),
       ('игрушка мягкая', 'хаги-ваги', true, 4),
       ('кресло комьютерное', 'кожаное из икеи', true, 3),
       ('сотовый телефон', 'нокиа', true, 2),
       ('Отвертка', 'Аккумуляторная отвертка', true, 1),
       ('сотовый телефон', 'нокиа', true, 5);

INSERT INTO BOOKINGS
(START_DATE, END_DATE, ITEM_ID, BOOKER_ID, STATUS)
VALUES ({ts '2023-01-20 18:47:52.69'}, {ts '2023-11-17 18:47:52.69'}, 1, 1, 'WAITING'),
       ({ts '2022-09-17 18:47:52.69'}, {ts '2023-09-25 18:47:52.69'}, 2, 1, 'WAITING'),
       ({ts '2022-08-17 18:47:52.69'}, {ts '2022-09-01 18:47:52.69'}, 3, 1, 'REJECTED'),
       ({ts '2022-07-17 18:47:52.69'}, {ts '2022-08-26 18:47:52.69'}, 4, 1, 'APPROVED'),
       ({ts '2022-04-17 18:47:52.69'}, {ts '2023-08-26 18:47:52.69'}, 5, 1, 'APPROVED'),
       ({ts '2022-05-17 18:47:52.69'}, {ts '2023-08-26 18:47:52.69'}, 6, 1, 'APPROVED'),
       ({ts '2022-05-17 18:47:52.69'}, {ts '2023-08-26 18:47:52.69'}, 8, 6, 'APPROVED'),
       ({ts '2022-06-17 18:47:52.69'}, {ts '2022-07-01 18:47:52.69'}, 4, 1, 'CANCELED');


INSERT INTO REQUESTS
(DESCRIPTION, REQUESTER_ID, CREATED_DATE)
VALUES('I wanna this item', 6, '2022-12-29 18:47:52.69');

