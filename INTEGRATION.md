# 장바구니(Cart) ↔ 메뉴(Menu) 도메인 통합 규격

`feature/cart` 브랜치는 팀원 담당인 **Menu · MenuOption** 도메인에 `@ManyToOne`으로 의존합니다.
아래 심볼이 있어야 `feature/cart`가 컴파일됩니다. 메뉴 도메인 담당자는 이 규격에 맞춰 주세요.
(필드명이 다르면 `CartItem` / `CartItemResponseDto` / `CartService`의 getter 호출부만 맞추면 됩니다.)

## 1. `mutsa.delivery.domain.Menu` (엔티티)
장바구니가 사용하는 getter:

| 메서드 | 반환형 | 용도 |
| --- | --- | --- |
| `getId()` | `Long` | 식별자 |
| `getMenuName()` | `String` | 응답의 메뉴명 |
| `getPrice()` | `Long` | 단가 계산 |
| `getStock()` | `Integer` | 재고 검증 (null이면 검증 skip) |

## 2. `mutsa.delivery.domain.MenuOption` (엔티티)

| 메서드 | 반환형 | 용도 |
| --- | --- | --- |
| `getId()` | `Long` | 식별자 |
| `getOptionName()` | `String` | 응답의 옵션명 |
| `getExtraPrice()` | `Long` | 단가에 더할 추가금액 |

## 3. Repository (팀원 도메인 패키지 `mutsa.delivery.repository`)
```java
public interface MenuRepository extends JpaRepository<Menu, Long> {}
public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {}
```

## 4. 단가/총액 계산 규칙 (참고)
- 항목 단가 `unitPrice = menu.getPrice() + menuOption.getExtraPrice()`
- 항목 소계 `linePrice = unitPrice * quantity`
- 장바구니 총액 = 모든 항목 소계 합, 총수량 = 모든 항목 수량 합

## 5. 임시 처리 (통합 시 교체 가능)
- `CartService`에서 메뉴/옵션 미존재 시 `GeneralErrorCode.NOT_FOUND`를 던집니다.
  메뉴 도메인에 `MenuErrorCode.MENU_NOT_FOUND` 등이 생기면 그걸로 바꾸면 됩니다.
- 재고 부족은 `CartErrorCode.OUT_OF_STOCK`(CART_400_1)로 처리합니다.

> 통합 순서 제안: 메뉴 도메인(`feature/menu`)이 main에 머지된 뒤 `feature/cart`를 rebase → 컴파일 확인 → PR.
