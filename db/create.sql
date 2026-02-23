-- 1) Виды товаров
create table if not exists product_type (
                                            id bigserial primary key,
                                            name varchar(100) not null unique
    );

-- 2) Производители
create table if not exists manufacturer (
                                            id bigserial primary key,
                                            name varchar(150) not null,
    assembly_country varchar(100) not null,
    unique (name, assembly_country)
    );

-- 3) Товары
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

-- 4) Характеристики товара (key/value)
create table if not exists product_attribute (
                                                 id bigserial primary key,
                                                 product_id bigint not null references product(id) on delete cascade,

    attr_key varchar(100) not null,
    attr_value varchar(255) not null
    );

create index if not exists idx_attr_product on product_attribute(product_id);
create index if not exists idx_attr_key on product_attribute(attr_key);

-- 5) Клиенты
create table if not exists customer (
                                        id bigserial primary key,
                                        full_name varchar(200) not null,
    phone varchar(30),
    email varchar(200),
    address text,

    created_at timestamp not null default now()
    );

create unique index if not exists uq_customer_email on customer(email) where email is not null;

-- 6) Заказы
do $$
begin
    if not exists (select 1 from pg_type where typname = 'order_status') then
create type order_status as enum ('processing', 'packed', 'delivered');
end if;
end $$;

create table if not exists orders (
                                      id bigserial primary key,
                                      customer_id bigint not null references customer(id),

    created_at timestamp not null default now(),
    status order_status not null default 'processing',

    delivery_address text not null,
    delivery_time_window varchar(100),

    total_amount numeric(12,2) not null default 0 check (total_amount >= 0)
    );

create index if not exists idx_orders_customer on orders(customer_id);
create index if not exists idx_orders_status on orders(status);

-- 7) Позиции заказа
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
