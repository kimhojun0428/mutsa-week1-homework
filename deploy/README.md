# 운영 서버 배포 가이드

이 디렉터리의 파일은 특정 서버 정보가 포함되지 않은 배포 템플릿이다. 실제 서버에 적용하기 전에 도메인, 실행 사용자, 설치 경로를 운영 환경에 맞게 변경해야 한다.

## 파일 구성

- `spring-app.service`: Spring Boot 애플리케이션을 systemd 서비스로 실행하는 템플릿
- `apache-reverse-proxy.conf`: Apache에서 HTTPS 요청을 Spring Boot로 전달하는 템플릿
- `../.env.example`: 애플리케이션에 필요한 환경변수 목록

실제 `.env`에는 DB 비밀번호와 JWT 키가 들어가므로 Git에 커밋하지 않는다.

## 1. 템플릿 값 변경

다음 예시값을 실제 운영 환경에 맞게 변경한다.

| 예시값 | 설명 |
| --- | --- |
| `api.example.com` | 백엔드 API 도메인 |
| `frontend.example.com` | CORS를 허용할 프론트엔드 도메인 |
| `appuser` | 애플리케이션을 실행할 Linux 사용자와 그룹 |
| `/opt/spring-app` | JAR와 `.env`를 저장할 디렉터리 |
| `app_db`, `app_user` | 애플리케이션 전용 DB와 DB 사용자 |

## 2. 비밀값 생성

JWT 키와 DB 비밀번호는 운영 서버에서 생성한다.

```bash
openssl rand -base64 64 # JWT_SECRET
openssl rand -base64 32 # DB_PASSWORD
```

`.env.example`을 참고해 운영 서버의 설치 디렉터리에 `.env`를 만들고 읽기 권한을 제한한다.

```bash
sudo install -d -o appuser -g appuser -m 750 /opt/spring-app
sudo install -o appuser -g appuser -m 600 .env /opt/spring-app/.env
```

## 3. 애플리케이션 빌드 및 설치

```bash
./gradlew clean build
sudo install -o appuser -g appuser -m 640 \
  build/libs/mutsa-delivery-0.0.1-SNAPSHOT.jar \
  /opt/spring-app/app.jar
```

## 4. systemd 서비스 등록

템플릿의 사용자와 경로를 먼저 수정한 다음 등록한다.

```bash
sudo install -o root -g root -m 644 \
  deploy/spring-app.service \
  /etc/systemd/system/spring-app.service
sudo systemctl daemon-reload
sudo systemctl enable --now spring-app.service
sudo systemctl status spring-app.service
```

`enable`을 적용했기 때문에 서버가 재부팅돼도 서비스가 자동으로 시작된다. 애플리케이션이 비정상 종료되면 `Restart=on-failure` 설정에 따라 다시 시작된다.

## 5. Apache 리버스 프록시 등록

도메인과 인증서 경로를 실제 값으로 변경하고 Apache 설정을 등록한다.

```bash
sudo install -o root -g root -m 644 \
  deploy/apache-reverse-proxy.conf \
  /etc/apache2/sites-available/spring-app.conf
sudo a2enmod proxy proxy_http ssl headers rewrite
sudo a2ensite spring-app.conf
sudo apache2ctl configtest
sudo systemctl reload apache2
```

Apache는 HTTPS 요청을 `127.0.0.1:8080`에서 실행 중인 Spring Boot 애플리케이션으로 전달한다.

## 6. 배포 확인

```bash
systemctl is-enabled spring-app.service
systemctl is-active spring-app.service
curl -i http://127.0.0.1:8080/api/health
curl -i https://api.example.com/api/health
```

systemd가 `enabled`, `active`이고 두 헬스체크가 모두 `200`을 반환하면 배포가 완료된 것이다.
