#!/bin/bash
echo "SSL init"

pemfile="/etc/ssl/certs/dhparam-2048.pem"
certfile="/etc/custom/certs/nginx-selfsigned.crt"

if [ ! -f "$pemfile" ]; then
  cd /etc/ssl/certs
  openssl dhparam -out dhparam-2048.pem 2048
fi

if [ ! -f "$certfile" ]; then
  cd /etc/custom/certs
  openssl req -x509 -nodes -days 365 -newkey rsa:2048 -subj "/C=RU/ST=Saratov/L=Saratov/O=None/CN=vzotov.ru" -keyout nginx-selfsigned.key -out nginx-selfsigned.crt
fi