#!/bin/bash

set -e  # Exit on error

echo "Installing PostgreSQL..."
sudo yum install -y postgresql postgresql-contrib

echo "Getting parameters from SSM..."
DB_URL=$(aws ssm get-parameter --name "/chatbot/db_name" --with-decryption --query "Parameter.Value" --output text)
DB_URL=$(aws ssm get-parameter --name "/chatbot/db_url" --with-decryption --query "Parameter.Value" --output text)
DB_USER=$(aws ssm get-parameter --name "/chatbot/db_user" --with-decryption --query "Parameter.Value" --output text)
DB_PASSWORD=$(aws ssm get-parameter --name "/chatbot/db_password" --with-decryption --query "Parameter.Value" --output text)
DB_PORT=$(aws ssm get-parameter --name "/chatbot/db_port" --with-decryption --query "Parameter.Value" --output text)

echo "Configuring PostgreSQL..."
sudo -u postgres psql <<EOF
CREATE DATABASE $DB_NAME;
CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
ALTER ROLE $DB_USER SET client_encoding TO 'utf8';
ALTER ROLE $DB_USER SET default_transaction_isolation TO 'read committed';
ALTER ROLE $DB_USER SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
EOF

echo "Enabling remote connections..."
sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/" /etc/postgresql/14/main/postgresql.conf
echo "host all all 0.0.0.0/0 md5" | sudo tee -a /etc/postgresql/14/main/pg_hba.conf
sudo systemctl restart postgresql