#!/bin/bash

set -e  # Exit on error

echo "Updating system..."
sudo apt update

echo "[Executing setup-aws.sh]"
./setup-aws.sh

echo "[Executing setup-db.sh]"
./setup-db.sh

echo "[Executing setup-docker.sh]"
./setup-docker.sh

echo "EC2 instance setup complete!"