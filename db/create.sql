-- 1) Типы для заказа
do $$
    begin
        if not exists (select 1 from pg_type where typname = 'order_status') then
            create type order_status as enum ('cart', 'processing', 'packed', 'delivered', 'cancelled');
        end if;
    end $$;

-- 2) Пользователи приложения
create table if not exists app_user (
                                        id bigserial primary key,
                                        email varchar(320) not null unique,
                                        password_hash varchar(200) not null,
                                        role varchar(20) not null check (role in ('USER', 'MANAGER', 'ADMIN')),
                                        enabled boolean not null default true,

                                        full_name varchar(200) not null,
                                        phone varchar(30),
                                        address text,

                                        created_at timestamp not null default now()
);

create index if not exists idx_app_user_role on app_user(role);

-- 3) Виды товаров
create table if not exists product_type (
                                            id bigserial primary key,
                                            name varchar(100) not null unique
);

-- 4) Производители
create table if not exists manufacturer (
                                            id bigserial primary key,
                                            name varchar(150) not null,
                                            assembly_country varchar(100) not null,
                                            unique (name, assembly_country)
);

-- 5) Товары
create table if not exists product (
                                       id bigserial primary key,
                                       type_id bigint not null references product_type(id),
                                       manufacturer_id bigint not null references manufacturer(id),

                                       name varchar(200) not null,
                                       description text,

                                       price numeric(12,2) not null check (price >= 0),
                                       stock_qty integer not null check (stock_qty >= 0),

                                       created_at timestamp not null default now(),
                                       updated_at timestamp not null default now()
);

create index if not exists idx_product_type on product(type_id);
create index if not exists idx_product_manufacturer on product(manufacturer_id);
create index if not exists idx_product_price on product(price);

-- 6) Характеристики товара
create table if not exists product_type_attribute (
                                                      id bigserial primary key,
                                                      product_type_id bigint not null references product_type(id) on delete cascade,
                                                      name varchar(100) not null,
                                                      sort_order integer not null default 0,
                                                      is_required boolean not null default false,
                                                      unique (product_type_id, name)
);

create index if not exists idx_pta_type on product_type_attribute(product_type_id);
create index if not exists idx_pta_type_sort on product_type_attribute(product_type_id, sort_order);


-- 7) Значения характеристик товара

create table if not exists product_attribute_value (
                                                       id bigserial primary key,
                                                       product_id bigint not null references product(id) on delete cascade,
                                                       product_type_attribute_id bigint not null references product_type_attribute(id),
                                                       value varchar(255) not null,
                                                       unique (product_id, product_type_attribute_id)
);

create index if not exists idx_pav_product on product_attribute_value(product_id);
create index if not exists idx_pav_attr on product_attribute_value(product_type_attribute_id);

-- 7) Заказы
create table if not exists orders (
                                      id bigserial primary key,
                                      user_id bigint not null references app_user(id),

                                      created_at timestamp not null default now(),
                                      status order_status not null default 'cart',

                                      delivery_address text,
                                      delivery_time_window varchar(100),

                                      total_amount numeric(12,2) not null default 0 check (total_amount >= 0)
);

create index if not exists idx_orders_user on orders(user_id);
create index if not exists idx_orders_status on orders(status);
create index if not exists idx_orders_user_status on orders(user_id, status);

create unique index if not exists uq_orders_user_cart
    on orders(user_id)
    where status = 'cart';

-- 8) Позиции заказа
create table if not exists order_item (
                                          id bigserial primary key,
                                          order_id bigint not null references orders(id) on delete cascade,
                                          product_id bigint not null references product(id),

                                          qty integer not null check (qty > 0),
                                          unit_price numeric(12,2) not null check (unit_price >= 0),

                                          line_total numeric(12,2) generated always as (qty * unit_price) stored
);

create index if not exists idx_order_item_order on order_item(order_id);
create index if not exists idx_order_item_product on order_item(product_id);