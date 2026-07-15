# 배달앱 API 연동 가이드 (프론트엔드용)

> ⚠️ **본 명세는 JWT 인증 적용 후의 "최종 계약" 기준입니다.**
> 현재 백엔드 구현이 진행 중이며, 프론트는 이 문서를 기준으로 병렬 개발을 시작하면 됩니다.
> 실제 배포 서버 주소와 인터랙티브 문서(Swagger UI)는 배포 완료 후 추가 공유합니다.
>
> 최종 수정: 2026-07-11 · 작성: 백엔드(유현준)

---

## 1. 기본 정보 (공통 규약)

### Base URL

| 환경 | 주소 |
| --- | --- |
| 로컬 개발 | `http://localhost:8080` |
| 배포 | `TBD` (배포 후 교체 예정) |

### 공통 응답 형식 (Response Envelope)

모든 API는 **성공/실패 상관없이** 아래 봉투(envelope) 구조로 응답합니다.

```json
{
  "success": true,
  "code": "COMMON_200_1",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { },
  "error": null
}
```

| 필드 | 설명 |
| --- | --- |
| `success` | 성공 여부 (`true` / `false`) |
| `code` | 결과 코드 (성공: `COMMON_200_1`·`COMMON_201_1`, 실패: 에러코드) |
| `message` | 사람이 읽을 메시지 |
| `data` | 실제 응답 데이터 (실패 시 `null`) |
| `error` | 에러 상세 (성공 시 `null`, 검증 실패 시 필드별 메시지 맵) |

> 💡 프론트는 항상 `data` 안에서 실제 값을 꺼내 쓰세요. (예: 로그인 토큰은 `response.data.data.accessToken`)

**실패 응답 예시**

```json
{
  "success": false,
  "code": "USER_409_1",
  "message": "이미 사용 중인 이메일입니다.",
  "data": null,
  "error": null
}
```

**검증 실패(필수값 누락 등) 예시** — `error`에 필드별 메시지가 담깁니다.

```json
{
  "success": false,
  "code": "INVALID_REQUEST",
  "message": "필수 항목이 누락되었습니다.",
  "data": null,
  "error": {
    "email": "이메일 형식이 올바르지 않습니다.",
    "password": "비밀번호는 8자 이상이어야 합니다."
  }
}
```

### 인증 방식 (JWT)

- 로그인 성공 시 발급되는 **Access Token**을 저장했다가, 인증이 필요한 요청마다 헤더에 담아 보냅니다.

```
Authorization: Bearer {accessToken}
```

- `Bearer` 와 토큰 사이에 **공백 1칸** 필수.
- **401 Unauthorized** = 미인증 (토큰 없음 / 형식 오류 / 서명 오류 / 만료)
- **403 Forbidden** = 인증은 됐지만 권한이 없음

> ⚠️ **마이그레이션 주의:** 1주차에 쓰던 `X-User-Id` 헤더 방식은 **폐기**됩니다.
> 프론트는 처음부터 `Authorization: Bearer` 토큰 방식으로 구현해주세요.

---

## 2. 인증 API (로그인 불필요)

### 회원가입

```
POST /api/auth/signup
```

**Request Body**

```json
{
  "email": "test@example.com",
  "password": "password1234",
  "name": "멋쟁이"
}
```

| 필드 | 타입 | 제약 |
| --- | --- | --- |
| `email` | string | 필수, 이메일 형식, 중복 불가 |
| `password` | string | 필수, 8자 이상 |
| `name` | string | 필수 |

**Response (201 Created)**

```json
{
  "success": true,
  "code": "COMMON_201_1",
  "message": "새로운 리소스가 성공적으로 생성되었습니다.",
  "data": { "id": 1, "name": "멋쟁이", "email": "test@example.com", "credit": 0 },
  "error": null
}
```

**주요 에러**: `INVALID_REQUEST`(400, 형식/필수값), `USER_409_1`(409, 이메일 중복)

---

### 로그인

```
POST /api/auth/login
```

**Request Body**

```json
{
  "email": "test@example.com",
  "password": "password1234"
}
```

**Response (200 OK)**

```json
{
  "success": true,
  "code": "COMMON_200_1",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer" },
  "error": null
}
```

**주요 에러**: `LOGIN_FAILED`(401, 존재하지 않는 사용자 또는 비밀번호 불일치)

> 🔐 보안상 "없는 이메일"과 "틀린 비밀번호"는 **동일한 에러(`LOGIN_FAILED`)** 로 응답합니다.

---

## 3. 사용자 API (로그인 필요)

### 내 정보 조회

```
GET /api/users/me
Authorization: Bearer {accessToken}
```

**Response (200 OK)** — `data`: `{ "id": 1, "name": "멋쟁이", "email": "test@example.com", "credit": 0 }`

### 내 정보 수정

```
PATCH /api/users/me
```

**Request Body** (둘 다 선택, 보낼 값만 변경)

```json
{ "name": "새이름", "password": "newpassword1234" }
```

Response `data`: 수정된 유저 `{ id, name, email, credit }` *(성공코드 `COMMON_201_1`)*

### 회원 탈퇴

```
DELETE /api/users/me
```

Response (200 OK) — `data: null`

---

## 4. 상점 · 메뉴 API (로그인 불필요)

### 상점 목록 조회

```
GET /api/shops
```

Response `data` (배열):

```json
[
  { "shopId": 1, "name": "냠냠 분식", "contact": "02-320-0001", "location": "T101", "category": "SNACK" }
]
```

`category` 값: `SNACK` | `ETC`

### 특정 상점의 메뉴 목록 조회

```
GET /api/shops/{shopId}/menus
```

Response `data` (배열):

```json
[
  {
    "menuId": 1,
    "shopId": 1,
    "name": "떡볶이",
    "description": "맛있는 떡볶이",
    "price": 12000,
    "stock": 10,
    "options": [
      { "menuOptionId": 1, "name": "안 맵게", "additionalPrice": 0 },
      { "menuOptionId": 2, "name": "맵게", "additionalPrice": 0 }
    ]
  }
]
```

---

## 5. 장바구니 API (로그인 필요)

| Method | URL | 설명 | 성공 |
| --- | --- | --- | --- |
| `GET` | `/api/cart` | 내 장바구니 조회 | 200 |
| `POST` | `/api/cart/items` | 메뉴 담기 | 201 |
| `PATCH` | `/api/cart/items/{itemId}` | 수량 변경 | 200 |
| `DELETE` | `/api/cart/items/{itemId}` | 항목 삭제 | 200 |

**GET /api/cart** — Response `data`:

```json
{
  "items": [
    { "cartItemId": 1, "menuId": 1, "menuOptionId": 2, "name": "떡볶이", "optionName": "맵게", "price": 12000, "quantity": 2 }
  ],
  "totalPrice": 24000
}
```

**POST /api/cart/items** — Request:

```json
{ "menuId": 1, "menuOptionId": 2, "quantity": 2 }
```

`quantity`는 1 이상. **PATCH**는 `{ "quantity": 3 }` 만 보냅니다.

**주요 에러**: `CART_404_1/2`(404), `CART_400_1`(400, 재고 부족)

---

## 6. 주소 API (로그인 필요)

| Method | URL | 설명 | 성공 |
| --- | --- | --- | --- |
| `POST` | `/api/addresses` | 주소 등록 | 201 |
| `GET` | `/api/addresses` | 내 주소 목록 | 200 |
| `GET` | `/api/addresses/{addressId}` | 주소 단건 조회 | 200 |
| `PUT` | `/api/addresses/{addressId}` | 주소 수정 | 200 |
| `DELETE` | `/api/addresses/{addressId}` | 주소 삭제 | 200 |

**POST / PUT Request Body**

```json
{
  "addressName": "집",
  "address": "서울시 마포구 와우산로 94",
  "zipCode": "04066",
  "phoneNumber": "010-1234-5678"
}
```

| 필드 | 제약 |
| --- | --- |
| `addressName` | 필수 |
| `address` | 필수 |
| `zipCode` | 필수, 숫자 5자리 |
| `phoneNumber` | 형식 `010-1234-5678` (또는 지역번호) |

Response `data`: `{ "addressId": 1, "addressName": "집", "address": "...", "zipCode": "04066", "phoneNumber": "010-1234-5678" }`

**주요 에러**: `ADDRESS_404_1`(404), `ADDRESS_409_1/2`(409, 중복)

---

## 7. 크레딧 API (로그인 필요)

### 크레딧 변동 (충전/사용/환불)

```
POST /api/credits
```

**Request Body**

```json
{ "amount": 10000, "type": "CHARGE" }
```

| 필드 | 제약 |
| --- | --- |
| `amount` | 필수, 1 이상 |
| `type` | `CHARGE`(충전) / `USE`(사용) / `REFUND`(환불) |

**Response (201)** `data`:

```json
{ "id": 1, "amount": 10000, "type": "CHARGE", "created_at": "2026-07-11T12:00:00" }
```

### 크레딧 내역 조회

```
GET /api/credits
```

Response `data`: 위 객체의 배열. `created_at`은 **snake_case** 주의.

**주요 에러**: `CREDIT_400_1`(잔액 부족), `CREDIT_400_2`(충전 금액 오류)

---

## 8. 에러 코드 표

봉투의 `code` 필드로 내려옵니다. 프론트는 이 코드로 분기 처리하세요.

| HTTP | code | 의미 |
| --- | --- | --- |
| 400 | `INVALID_REQUEST` | 필수값 누락 / 잘못된 JSON |
| 400 | `INVALID_TYPE` | 입력 타입 오류 |
| 401 | `UNAUTHORIZED` | 인증 필요 / 토큰 없음 |
| 401 | `LOGIN_FAILED` | 로그인 실패 (없는 유저·비번 불일치) |
| 401 | `INVALID_TOKEN` | 토큰 형식·서명 오류 |
| 401 | `EXPIRED_TOKEN` | 토큰 만료 |
| 403 | `FORBIDDEN` | 권한 없음 |
| 404 | `NOT_FOUND` / `USER_404_1` / `CART_404_*` / `ADDRESS_404_1` / `SHOP_404_1` | 리소스 없음 |
| 409 | `USER_409_1` | 이메일 중복 |
| 409 | `ADDRESS_409_1/2` | 주소 중복 |
| 400 | `CART_400_1` | 재고 부족 |
| 400 | `CREDIT_400_1/2` | 크레딧 잔액/금액 오류 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 오류 |

---

## 9. JWT 토큰 저장 · 전달 (프론트 구현 가이드)

1. 로그인 성공 → 응답의 `data.accessToken` 저장 (예: `localStorage`).
2. 인증이 필요한 요청마다 헤더 추가: `Authorization: Bearer {accessToken}`.
3. axios 인터셉터로 자동 첨부 권장:

```js
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

4. 응답이 `401`(`EXPIRED_TOKEN`/`INVALID_TOKEN`)이면 로그인 화면으로 유도.

**체크리스트**: 필드명이 `accessToken`인지 · `Bearer ` 뒤 공백 1칸 · 헤더 이름 `Authorization` · 토큰 만료 여부.

---

## 10. 테스트 계정

JWT + 비밀번호 암호화 적용 후 아래 더미 계정으로 로그인 가능합니다.

| email | password |
| --- | --- |
| `kimmutsa@mutsa.com` | `12345678` |
| `leehongik@hongik.ac.kr` | `87654321` |
| `parkchulsu@naver.com` | `abcdefgh` |

---

## 11. 프론트 → 백엔드에 알려주세요 (CORS)

브라우저 CORS 허용 목록에 넣어야 하므로 **아래 주소를 백엔드에 공유**해주세요.

- [ ] 프론트 **로컬 개발 주소** (예: `http://localhost:5173`)
- [ ] 프론트 **배포 예정 주소** (예: `https://xxx.vercel.app`)

> 이 주소가 등록되지 않으면 브라우저에서 CORS 오류로 요청이 차단됩니다.
> (Postman은 CORS 영향을 받지 않으니, 브라우저에서 꼭 테스트해주세요.)

## 12. 인증 필요 여부 요약

| 인증 불필요 | 인증 필요 (Bearer) |
| --- | --- |
| `POST /api/auth/signup` | `GET/PATCH/DELETE /api/users/me` |
| `POST /api/auth/login` | `*/api/cart/**` |
| `GET /api/shops` | `*/api/addresses/**` |
| `GET /api/shops/{shopId}/menus` | `*/api/credits/**` |
