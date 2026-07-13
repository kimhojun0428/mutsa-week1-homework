package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; // 💡 추가
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.orderGroup.OrderGroupDetailResponseDto;
import mutsa.delivery.dto.orderGroup.OrderGroupRequestDto;
import mutsa.delivery.dto.orderGroup.OrderGroupResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OrderGroup API", description = "주문그룹 도메인 API")
public interface OrderGroupApiDocs {

    @Operation(summary = "주문 생성 및 결제", description = "장바구니에 담긴 상품들을 기반으로 크레딧을 차감하고 상점별 주문서를 분기 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 및 결제 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "크레딧 잔액 부족(CREDIT_400_1) / 장바구니 비어있음(CART_400_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음(USER_404_1) / 배송지 없음(ADDRESS_404_1) / 장바구니 없음(CART_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })

    ResponseEntity<GlobalResponse<OrderGroupResponseDto>> createOrderGroup(
            @Parameter(hidden = true) Long userId,
            @RequestBody OrderGroupRequestDto requestDto
    );

    @Operation(summary = "주문 취소", description = "주문 그룹 ID를 받아 하위 모든 주문을 취소하고 자원(크레딧, 재고)을 원복합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 및 환불 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 완료된 주문 취소 불가(ORDER_400_2)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문 내역 없음(ORDER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })

    ResponseEntity<GlobalResponse<Void>> cancelOrderGroup(
            @Parameter(hidden = true) Long userId,
            @PathVariable("orderGroupId") Long orderGroupId
    );

    @Operation(summary = "주문 상세 조회", description = "주문그룹 ID를 받아 배송지 정보, 상점별 주문서, 상세 상품 내역을 트리 구조로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문 내역을 찾을 수 없음(ORDER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })

    ResponseEntity<GlobalResponse<OrderGroupDetailResponseDto>> getOrderGroupDetail(
            @Parameter(hidden = true) Long userId,
            @PathVariable("orderGroupId") Long orderGroupId
    );
}