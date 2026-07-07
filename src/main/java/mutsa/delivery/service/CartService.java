package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.CartItem;
import mutsa.delivery.domain.Menu;
import mutsa.delivery.domain.MenuOption;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.cart.AddCartItemRequestDto;
import mutsa.delivery.dto.cart.CartItemResponseDto;
import mutsa.delivery.dto.cart.CartResponseDto;
import mutsa.delivery.dto.cart.UpdateQuantityRequestDto;
import mutsa.delivery.global.apiPayload.code.CartErrorCode;
import mutsa.delivery.global.apiPayload.code.GeneralErrorCode;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.CartItemRepository;
import mutsa.delivery.repository.CartRepository;
import mutsa.delivery.repository.MenuRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 장바구니 서비스.
 * <p>Menu·MenuOption 및 MenuRepository는 메뉴 도메인(팀원) 담당입니다.
 * MenuOption은 별도 Repository 없이 Menu.getOptions()로 조회합니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public CartItemResponseDto addMenuToCart(Long userId, AddCartItemRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.create(user)));

        Menu menu = menuRepository.findById(requestDto.menuId())
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND));

        // MenuOption은 해당 Menu에 속한 옵션 중에서 조회 (별도 Repository 없음)
        MenuOption menuOption = menu.getOptions().stream()
                .filter(option -> option.getId().equals(requestDto.menuOptionId()))
                .findFirst()
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND));

        Optional<CartItem> optionalCartItem =
                cartItemRepository.findByCartAndMenuAndMenuOption(cart, menu, menuOption);

        int currentQuantity = optionalCartItem.map(CartItem::getQuantity).orElse(0);
        int totalQuantity = currentQuantity + requestDto.quantity();

        verifyStock(menu, totalQuantity);

        CartItem resultItem = optionalCartItem
                .map(item -> {
                    item.increaseQuantity(requestDto.quantity());
                    return item;
                })
                .orElseGet(() -> cartItemRepository.save(
                        CartItem.create(cart, menu, menuOption, requestDto.quantity())
                ));

        return CartItemResponseDto.from(resultItem);
    }

    public CartResponseDto getCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        return cartRepository.findByUser(user)
                .map(CartResponseDto::from)
                .orElseGet(CartResponseDto::empty);
    }

    @Transactional
    public CartItemResponseDto updateCartItemQuantity(Long userId, Long cartItemId, UpdateQuantityRequestDto requestDto) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ProjectException(CartErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.validateUser(userId);

        verifyStock(cartItem.getMenu(), requestDto.quantity());

        cartItem.updateQuantity(requestDto.quantity());

        return CartItemResponseDto.from(cartItem);
    }

    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ProjectException(CartErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.validateUser(userId);

        cartItemRepository.delete(cartItem);
    }

    private void verifyStock(Menu menu, int requiredQuantity) {
        Integer stock = menu.getStock();
        if (stock != null && stock < requiredQuantity) {
            throw new ProjectException(CartErrorCode.OUT_OF_STOCK);
        }
    }
}
