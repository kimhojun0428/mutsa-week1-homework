# Production deployment

The production service reads secrets from `/opt/mutsa-delivery/.env`. Never commit
that file. Use `.env.example` as the list of required keys.

Generate secrets directly on the server:

```bash
openssl rand -base64 64 # JWT_SECRET
openssl rand -base64 32 # DB_PASSWORD
```

Build and install the application:

```bash
./gradlew clean build
sudo install -d -o ubuntu -g ubuntu -m 750 /opt/mutsa-delivery
sudo install -o ubuntu -g ubuntu -m 640 .env /opt/mutsa-delivery/.env
sudo install -o ubuntu -g ubuntu -m 640 build/libs/mutsa-delivery-0.0.1-SNAPSHOT.jar /opt/mutsa-delivery/app.jar
sudo install -o root -g root -m 644 deploy/mutsa-delivery.service /etc/systemd/system/mutsa-delivery.service
sudo systemctl daemon-reload
sudo systemctl enable --now mutsa-delivery.service
```

Apache proxies `mutsa.dev.me.kr` to the application on `127.0.0.1:8080`.
