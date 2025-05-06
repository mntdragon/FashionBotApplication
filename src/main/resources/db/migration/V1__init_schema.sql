CREATE TABLE watch_list (
  id SERIAL PRIMARY KEY,
  product_url VARCHAR(1024) NOT NULL,
  variant_sku VARCHAR(255),
  threshold_pct DOUBLE PRECISION NOT NULL
  );

CREATE TABLE price_history (
  id SERIAL PRIMARY KEY,
  watch_item_id INTEGER NOT NULL REFERENCES watch_list(id),
  checked_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  price NUMERIC(10,2) NOT NULL,
   name VARCHAR(255)
);