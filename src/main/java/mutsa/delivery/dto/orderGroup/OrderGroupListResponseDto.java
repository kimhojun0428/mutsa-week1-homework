package mutsa.delivery.dto.orderGroup;

import java.time.LocalDateTime;

public record OrderGroupListResponseDto(
        Long orderGroupId,
        String title,
        Long totalPrice,
        String orderStatus,
        LocalDateTime createdAt
) {
}
