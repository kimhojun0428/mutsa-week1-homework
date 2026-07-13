package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.CartApiDocs;
import mutsa.delivery.dto.cart.AddCartItemRequestDto;
import mutsa.delivery.dto.cart.CartItemResponseDto;
import mutsa.delivery.dto.cart.CartResponseDto;
import mutsa.delivery.dto.cart.UpdateQuantityRequestDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.config.security.AuthUser;
import mutsa.delivery.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController implements CartApiDocs {

    private final CartService cartService;

    @Override
    @PostMapping("/items")
    public ResponseEntity<GlobalResponse<CartItemResponseDto>> addCartItem(
            @AuthUser Long userId,
            @Valid
            @RequestBody AddCartItemRequestDto requestDto
    ) {
        CartItemResponseDto responseDto = cartService.addMenuToCart(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<CartResponseDto>> getCart(
            @AuthUser Long userId
    ) {
        CartResponseDto responseDto = cartService.getCart(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<GlobalResponse<CartItemResponseDto>> updateCartItem(
            @AuthUser Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid
            @RequestBody UpdateQuantityRequestDto requestDto
    ) {
        CartItemResponseDto responseDto = cartService.updateCartItemQuantity(userId, itemId, requestDto);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<GlobalResponse<Void>> deleteCartItem(
            @AuthUser Long userId,
            @PathVariable("itemId") Long itemId
    ) {
        cartService.deleteCartItem(userId, itemId);

        return ResponseEntity.ok(GlobalResponse.onSuccess());
    }
}
