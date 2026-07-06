package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.cart.AddCartItemRequestDto;
import mutsa.delivery.dto.cart.CartItemResponseDto;
import mutsa.delivery.dto.cart.CartResponseDto;
import mutsa.delivery.dto.cart.UpdateQuantityRequestDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Cart API", description = "장바구니 도메인 API")
public interface CartApiDocs {

    @Operation(summary = "장바구니 담기", description = "메뉴+옵션을 장바구니에 담습니다. 동일 조합이면 수량을 합칩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장바구니 담기 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "재고 부족(CART_400_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자/메뉴/옵션 조회 실패",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<CartItemResponseDto>> addCartItem(Long userId, AddCartItemRequestDto requestDto);

    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니 항목과 총액/총수량을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<CartResponseDto>> getCart(Long userId);

    @Operation(summary = "장바구니 수량 변경", description = "장바구니 항목의 수량을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수량 변경 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "재고 부족(CART_400_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니 항목 조회 실패(CART_404_2)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<CartItemResponseDto>> updateCartItem(Long userId, Long itemId, UpdateQuantityRequestDto requestDto);

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 항목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니 항목 조회 실패(CART_404_2)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 권한 없음(USER_403_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<Void>> deleteCartItem(Long userId, Long itemId);
}
