
-- Two products to watch
INSERT INTO watch_list (product_url, variant_sku, threshold_pct) VALUES
  ('https://www.zalando.fi/nike-sportswear-air-force-1-07-lv8rugby-unisex-matalavartiset-tennarit-light-armory-blueanthracitewhite-ni115p00b-k11.html', 'NI115P00B-K110035000', 10.0,),
  ('https://www.zalando.fi/nike-sportswear-air-force-1-matalavartiset-tennarit-whitedeep-royal-blue-ni112p00b-a11.html', 'NI112P00B-A110060000', 15.0);
  ('https://www.zalando.fi/adidas-originals-tee-berlin-unisex-printtipaita-collegiate-green-ad12100gs-m11.html', 'AD12100GS-M1100XS000', 15.0);

-- Price history snapshots (timestamps relative to now)
INSERT INTO price_history (watch_item_id, checked_at, price) VALUES
  (1, NOW() - INTERVAL '2 days', 97.95, 'nike af1 armory'),
  (1, NOW() - INTERVAL '1 day', 129.00, 'nike af1 armory'),
  (2, NOW() - INTERVAL '3 days', 130.35,'nike af1 royal'),
  (2, NOW() - INTERVAL '1 day', 151.00),'nike af1 royal';