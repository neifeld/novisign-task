#!/bin/sh
set -e

# Wait for PostgreSQL
/wait-for-it.sh postgres 5432

# Wait for Kafka
/wait-for-it.sh kafka 9092

# Start the application
exec java -jar /app/app.jar