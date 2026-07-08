package mutsa.delivery.dto.orderGroup;

import java.time.LocalDateTime;
import java.util.List;

public record OrderGroupDetailResponseDto(
        Long orderGroupId,
        Long totalPrice,
        String address,
        String addressName,
        String phone,
        LocalDateTime createdAt,
        List<OrderDetailDto> orders // 상점별 주문서 목록
) {
    public record OrderDetailDto(
            Long orderId,
            String shopName,       // 상점명
            String orderStatus,    // 주문 상태 (ORDERED, DELIVERING 등)
            List<OrderItemDto> orderItems
    ) {}

    public record OrderItemDto(
            String menuName,       // 주문 당시 메뉴명 스냅샷
            Long unitPrice,     // 개당 가격 스냅샷
            Integer quantity,      // 수량
            String optionName,     // 옵션명 스냅샷
            Long additionalPrice   // 옵션 추가금액 스냅샷
    ) {}
}
