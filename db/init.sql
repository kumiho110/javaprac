insert into product_type(name) values
                                   ('Телевизоры'),
                                   ('Холодильники'),
                                   ('Стиральные машины')
    on conflict do nothing;

insert into manufacturer(name, assembly_country) values
                                                     ('Samsung', 'Vietnam'),
                                                     ('LG', 'Poland'),
                                                     ('Bosch', 'Germany')
    on conflict do nothing;


insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, m.id, 'Samsung 55" 4K UHD', 'Телевизор 55 дюймов', 54990.00, 7
from product_type pt, manufacturer m
where pt.name='Телевизоры' and m.name='Samsung' and m.assembly_country='Vietnam';

insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, m.id, 'LG NoFrost 360L', 'Холодильник двухкамерный', 69990.00, 4
from product_type pt, manufacturer m
where pt.name='Холодильники' and m.name='LG' and m.assembly_country='Poland';

insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, m.id, 'Bosch 7kg 1200rpm', 'Стиральная машина', 45990.00, 6
from product_type pt, manufacturer m
where pt.name='Стиральные машины' and m.name='Bosch' and m.assembly_country='Germany';

-- характеристики
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'diagonal_inch', '55' from product p where p.name='Samsung 55" 4K UHD';
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'resolution', '3840x2160' from product p where p.name='Samsung 55" 4K UHD';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'volume_l', '360' from product p where p.name='LG NoFrost 360L';
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'cameras', '2' from product p where p.name='LG NoFrost 360L';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'max_load_kg', '7' from product p where p.name='Bosch 7kg 1200rpm';
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'spin_rpm', '1200' from product p where p.name='Bosch 7kg 1200rpm';


-- Телевизоры (Samsung/LG/Bosch)
with pt as (select id from product_type where name='Телевизоры'),
     s as (select id from manufacturer where name='Samsung' and assembly_country='Vietnam'),
     l as (select id from manufacturer where name='LG' and assembly_country='Poland'),
     b as (select id from manufacturer where name='Bosch' and assembly_country='Germany')
insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, s.id,
       'Samsung TV Series ' || gs || ' 4K',
       'Телевизор серия ' || gs,
       29990 + gs*1200,
       (gs % 15)
from pt, s, generate_series(1, 12) gs
union all
select pt.id, l.id,
       'LG OLED Line ' || gs,
       'OLED телевизор линейка ' || gs,
       79990 + gs*2500,
       (10 + gs % 8)
from pt, l, generate_series(1, 8) gs
union all
select pt.id, b.id,
       'Bosch SmartTV ' || gs,
       'Телевизор Bosch ' || gs,
       39990 + gs*1700,
       (5 + gs % 10)
from pt, b, generate_series(1, 6) gs;

-- Холодильники (Samsung/LG/Bosch)
with pt as (select id from product_type where name='Холодильники'),
     s as (select id from manufacturer where name='Samsung' and assembly_country='Vietnam'),
     l as (select id from manufacturer where name='LG' and assembly_country='Poland'),
     b as (select id from manufacturer where name='Bosch' and assembly_country='Germany')
insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, s.id,
       'Samsung Fridge ' || gs || ' NoFrost',
       'Холодильник NoFrost серия ' || gs,
       59990 + gs*1800,
       (3 + gs % 7)
from pt, s, generate_series(1, 7) gs
union all
select pt.id, l.id,
       'LG Fridge ' || gs || ' NoFrost',
       'Холодильник LG серия ' || gs,
       54990 + gs*1600,
       (2 + gs % 6)
from pt, l, generate_series(1, 7) gs
union all
select pt.id, b.id,
       'Bosch Fridge ' || gs || ' Eco',
       'Холодильник Bosch Eco ' || gs,
       64990 + gs*1900,
       (4 + gs % 5)
from pt, b, generate_series(1, 6) gs;

-- Стиральные машины (Samsung/LG/Bosch)
with pt as (select id from product_type where name='Стиральные машины'),
     s as (select id from manufacturer where name='Samsung' and assembly_country='Vietnam'),
     l as (select id from manufacturer where name='LG' and assembly_country='Poland'),
     b as (select id from manufacturer where name='Bosch' and assembly_country='Germany')
insert into product(type_id, manufacturer_id, name, description, price, stock_qty)
select pt.id, s.id,
       'Samsung Washer ' || gs || ' 1200rpm',
       'Стиральная машина Samsung ' || gs,
       34990 + gs*900,
       (1 + gs % 12)
from pt, s, generate_series(1, 8) gs
union all
select pt.id, l.id,
       'LG Washer ' || gs || ' DirectDrive',
       'Стиральная машина LG ' || gs,
       36990 + gs*950,
       (2 + gs % 10)
from pt, l, generate_series(1, 8) gs
union all
select pt.id, b.id,
       'Bosch Washer ' || gs || ' 1400rpm',
       'Стиральная машина Bosch ' || gs,
       42990 + gs*1100,
       (3 + gs % 9)
from pt, b, generate_series(1, 10) gs;


-- Телевизоры: diagonal_inch, resolution, screen_format
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'diagonal_inch',
       (array['32','43','50','55','65','75'])[1 + (p.id % 6)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Телевизоры';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'resolution',
       (array['1920x1080','2560x1440','3840x2160'])[1 + (p.id % 3)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Телевизоры';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'screen_format',
       (array['16:9','21:9'])[1 + (p.id % 2)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Телевизоры';


-- Холодильники: volume_l, cameras, color
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'volume_l',
       (array['300','330','360','400','450'])[1 + (p.id % 5)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Холодильники';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'cameras',
       (array['1','2'])[1 + (p.id % 2)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Холодильники';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'color',
       (array['white','silver','black'])[1 + (p.id % 3)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Холодильники';


-- Стиральные машины: max_load_kg, spin_rpm, energy_class
insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'max_load_kg',
       (array['5','6','7','8','9'])[1 + (p.id % 5)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Стиральные машины';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'spin_rpm',
       (array['1000','1200','1400'])[1 + (p.id % 3)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Стиральные машины';

insert into product_attribute(product_id, attr_key, attr_value)
select p.id, 'energy_class',
       (array['A','A+','A++'])[1 + (p.id % 3)]
from product p
    join product_type pt on pt.id = p.type_id
where pt.name='Стиральные машины';


--клиенты
insert into customer(full_name, phone, email, address) values
                                                           ('Иванов Иван Иванович', '+7-900-111-22-33', 'ivanov@example.com', 'Москва, ул. Примерная, 1'),
                                                           ('Петров Петр Петрович', '+7-900-222-33-44', 'petrov@example.com', 'Санкт-Петербург, Невский, 10'),
                                                           ('Сидорова Анна Сергеевна', '+7-900-333-44-55', 'sidorova@example.com', 'Казань, Кремлевская, 5')
    on conflict do nothing;

-- 2 заказа для Петрова
with c as (select id, address from customer where email='petrov@example.com' limit 1)
insert into orders(customer_id, status, delivery_address, delivery_time_window, total_amount)
select c.id, 'processing', c.address, '10:00-14:00', 0 from c
union all
select c.id, 'packed', c.address, '18:00-21:00', 0 from c;

with oo as (
    select id as order_id
    from orders
    where customer_id = (select id from customer where email='petrov@example.com' limit 1)
order by id desc
    limit 2
    ),
    tv as (select id as product_id, price from product where name='Samsung 55" 4K UHD' limit 1),
    wm as (select id as product_id, price from product where name='Bosch 7kg 1200rpm' limit 1),
    fr as (select id as product_id, price from product where name='LG NoFrost 360L' limit 1)
insert into order_item(order_id, product_id, qty, unit_price)
select oo.order_id, tv.product_id, 1, tv.price from oo, tv
union all
select oo.order_id, wm.product_id, 1, wm.price from oo, wm
union all
select oo.order_id, fr.product_id, 1, fr.price from oo, fr;

-- пересчёт total_amount для всех заказов
update orders o
set total_amount = (
    select coalesce(sum(oi.line_total),0)
    from order_item oi
    where oi.order_id = o.id
);


insert into orders(customer_id, status, delivery_address, delivery_time_window, total_amount)
select c.id, 'processing', c.address, '18:00-21:00', 0
from customer c
where c.email='ivanov@example.com'
    returning id;

with o as (
    select id as order_id from orders
    order by id desc
    limit 1
    ),
    p1 as (select id as product_id, price from product where name='Samsung 55" 4K UHD' limit 1),
    p2 as (select id as product_id, price from product where name='Bosch 7kg 1200rpm' limit 1)
insert into order_item(order_id, product_id, qty, unit_price)
select o.order_id, p1.product_id, 1, p1.price from o, p1
union all
select o.order_id, p2.product_id, 1, p2.price from o, p2;

update orders o
set total_amount = (
    select coalesce(sum(oi.line_total),0)
    from order_item oi
    where oi.order_id = o.id
)
where o.id = (select id from orders order by id desc limit 1);