# FashionBotApplication

PriceWatch Bot is a Java 21 Spring Boot application that:

🎯 Monitors Zalando product prices via the Retailed API

💾 Persists watch items and historical prices (with product names) in PostgreSQL using Hibernate and Flyway

📩 Sends email alerts on price drops and fetch errors

🔒 Secures manual endpoints with JWT‑based authentication

## Features

Automated Checks: Hourly scheduled price checks via PriceCheckService.

Manual Trigger:

POST /api/token → issues a short‑lived JWT

GET /api/price-check → triggers checks and returns current name & price list

Persistence: Entities:

WatchItem (URL, variant SKU, threshold)

PriceHistory (with product name, timestamp, price)

## Prerequisites

Java 21

Maven 3.8+

PostgreSQL

SMTP server credentials for email alerts
