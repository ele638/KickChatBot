#!/bin/bash

set -e  # Exit on error

echo "Installing Docker..."
sudo apt install -y docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker ubuntu

echo "Installing Kubernetes (kubectl & kubeadm)..."
curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install -y kubectl kubeadm