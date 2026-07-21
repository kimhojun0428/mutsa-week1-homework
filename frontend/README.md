# 카카오 로그인 테스트 화면

## 실행

프로젝트 루트에서 아래 명령을 실행합니다.

```bash
cd frontend
npm run dev
```

그다음 브라우저에서 `http://localhost:5173`을 엽니다.

백엔드는 별도 터미널에서 `localhost:8080`으로 실행되어 있어야 하며,
`KAKAO_CLIENT_ID`와 `KAKAO_CLIENT_SECRET` 환경변수가 필요합니다.

카카오 개발자 콘솔에는 Redirect URI로 아래 주소를 등록합니다.

```text
http://localhost:8080/login/oauth2/code/kakao
```

로그인이 완료되면 화면이 Access Token을 저장하고 `GET /api/users/me`를 호출해
회원 정보를 출력합니다.
