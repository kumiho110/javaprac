-- 2) Типы товаров
insert into product_type (name)
values
    ('Телевизоры'),
    ('Холодильники'),
    ('Ноутбуки');

-- 3) Производители
insert into manufacturer (name, assembly_country)
values
    ('Samsung', 'Южная Корея'),
    ('LG', 'Южная Корея'),
    ('Bosch', 'Германия'),
    ('Lenovo', 'Китай'),
    ('ASUS', 'Китай');

-- 4) Шаблоны характеристик типов товаров

-- Телевизоры
insert into product_type_attribute (product_type_id, name, sort_order, is_required)
values
    ((select id from product_type where name = 'Телевизоры'), 'Диагональ', 1, true),
    ((select id from product_type where name = 'Телевизоры'), 'Разрешение', 2, true),
    ((select id from product_type where name = 'Телевизоры'), 'Smart TV', 3, true),
    ((select id from product_type where name = 'Телевизоры'), 'Тип матрицы', 4, false);

-- Холодильники
insert into product_type_attribute (product_type_id, name, sort_order, is_required)
values
    ((select id from product_type where name = 'Холодильники'), 'Общий объем', 1, true),
    ((select id from product_type where name = 'Холодильники'), 'Количество камер', 2, true),
    ((select id from product_type where name = 'Холодильники'), 'No Frost', 3, false),
    ((select id from product_type where name = 'Холодильники'), 'Класс энергопотребления', 4, false);

-- Ноутбуки
insert into product_type_attribute (product_type_id, name, sort_order, is_required)
values
    ((select id from product_type where name = 'Ноутбуки'), 'Диагональ экрана', 1, true),
    ((select id from product_type where name = 'Ноутбуки'), 'Процессор', 2, true),
    ((select id from product_type where name = 'Ноутбуки'), 'Оперативная память', 3, true),
    ((select id from product_type where name = 'Ноутбуки'), 'SSD', 4, true);

-- 5) Товары
insert into product (type_id, manufacturer_id, name, description, price, stock_qty)
values
    (
        (select id from product_type where name = 'Телевизоры'),
        (select id from manufacturer where name = 'Samsung' and assembly_country = 'Южная Корея'),
        'Samsung UE55AU7100U',
        '4K UHD телевизор с поддержкой Smart TV',
        54990.00,
        12
    ),
    (
        (select id from product_type where name = 'Телевизоры'),
        (select id from manufacturer where name = 'LG' and assembly_country = 'Южная Корея'),
        'LG 43UR78006LK',
        'Умный телевизор с экраном 43 дюйма',
        39990.00,
        8
    ),
    (
        (select id from product_type where name = 'Холодильники'),
        (select id from manufacturer where name = 'Bosch' and assembly_country = 'Германия'),
        'Bosch KGN39VL25R',
        'Двухкамерный холодильник с системой No Frost',
        67990.00,
        5
    ),
    (
        (select id from product_type where name = 'Ноутбуки'),
        (select id from manufacturer where name = 'Lenovo' and assembly_country = 'Китай'),
        'Lenovo IdeaPad Slim 5',
        'Универсальный ноутбук для работы и учебы',
        72990.00,
        10
    ),
    (
        (select id from product_type where name = 'Ноутбуки'),
        (select id from manufacturer where name = 'ASUS' and assembly_country = 'Китай'),
        'ASUS VivoBook 15',
        'Легкий ноутбук с SSD и Full HD экраном',
        65990.00,
        7
    );

-- 6) Значения характеристик товаров

-- Samsung UE55AU7100U
insert into product_attribute_value (product_id, product_type_attribute_id, value)
values
    (
        (select id from product where name = 'Samsung UE55AU7100U'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Диагональ'),
        '55"'
    ),
    (
        (select id from product where name = 'Samsung UE55AU7100U'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Разрешение'),
        '3840x2160'
    ),
    (
        (select id from product where name = 'Samsung UE55AU7100U'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Smart TV'),
        'Да'
    ),
    (
        (select id from product where name = 'Samsung UE55AU7100U'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Тип матрицы'),
        'VA'
    );

-- LG 43UR78006LK
insert into product_attribute_value (product_id, product_type_attribute_id, value)
values
    (
        (select id from product where name = 'LG 43UR78006LK'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Диагональ'),
        '43"'
    ),
    (
        (select id from product where name = 'LG 43UR78006LK'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Разрешение'),
        '3840x2160'
    ),
    (
        (select id from product where name = 'LG 43UR78006LK'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Smart TV'),
        'Да'
    ),
    (
        (select id from product where name = 'LG 43UR78006LK'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Телевизоры' and pta.name = 'Тип матрицы'),
        'IPS'
    );

-- Bosch KGN39VL25R
insert into product_attribute_value (product_id, product_type_attribute_id, value)
values
    (
        (select id from product where name = 'Bosch KGN39VL25R'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Холодильники' and pta.name = 'Общий объем'),
        '366 л'
    ),
    (
        (select id from product where name = 'Bosch KGN39VL25R'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Холодильники' and pta.name = 'Количество камер'),
        '2'
    ),
    (
        (select id from product where name = 'Bosch KGN39VL25R'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Холодильники' and pta.name = 'No Frost'),
        'Да'
    ),
    (
        (select id from product where name = 'Bosch KGN39VL25R'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Холодильники' and pta.name = 'Класс энергопотребления'),
        'A+'
    );

-- Lenovo IdeaPad Slim 5
insert into product_attribute_value (product_id, product_type_attribute_id, value)
values
    (
        (select id from product where name = 'Lenovo IdeaPad Slim 5'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Диагональ экрана'),
        '15.6"'
    ),
    (
        (select id from product where name = 'Lenovo IdeaPad Slim 5'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Процессор'),
        'Intel Core i5'
    ),
    (
        (select id from product where name = 'Lenovo IdeaPad Slim 5'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Оперативная память'),
        '16 ГБ'
    ),
    (
        (select id from product where name = 'Lenovo IdeaPad Slim 5'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'SSD'),
        '512 ГБ'
    );

-- ASUS VivoBook 15
insert into product_attribute_value (product_id, product_type_attribute_id, value)
values
    (
        (select id from product where name = 'ASUS VivoBook 15'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Диагональ экрана'),
        '15.6"'
    ),
    (
        (select id from product where name = 'ASUS VivoBook 15'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Процессор'),
        'AMD Ryzen 5'
    ),
    (
        (select id from product where name = 'ASUS VivoBook 15'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'Оперативная память'),
        '8 ГБ'
    ),
    (
        (select id from product where name = 'ASUS VivoBook 15'),
        (select pta.id
         from product_type_attribute pta
                  join product_type pt on pt.id = pta.product_type_id
         where pt.name = 'Ноутбуки' and pta.name = 'SSD'),
        '512 ГБ'
    );
