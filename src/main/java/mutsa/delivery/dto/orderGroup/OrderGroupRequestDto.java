package mutsa.delivery.dto.orderGroup;

import jakarta.validation.constraints.NotNull;

public record OrderGroupRequestDto(
        @NotNull(message = "배송지는 필수로 선택하셔야 합니다.")
        Long addressId
) {}
