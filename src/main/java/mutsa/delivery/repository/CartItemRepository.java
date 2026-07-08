package mutsa.delivery.repository;

import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.CartItem;
import mutsa.delivery.domain.Menu;
import mutsa.delivery.domain.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 같은 메뉴 + 같은 옵션 조합이 이미 담겨 있으면 수량만 증가시키기 위한 조회
    Optional<CartItem> findByCartAndMenuAndMenuOption(Cart cart, Menu menu, MenuOption menuOption);

    // 특정 장바구니(cartId)의 모든 항목을 삭제 (장바구니 비우기). 삭제된 건수 반환.
    long deleteAllByCart_Id(Long cartId);
}
