#!/bin/bash

set -e  # Exit on error

echo "Updating system..."
sudo apt update && sudo apt upgrade -y

echo "[Executing setup-db.sh]"
./setuo-db.sh

echo "[Executing setup-docker.sh]"
./setuo-docker.sh

echo "EC2 instance setup complete!"