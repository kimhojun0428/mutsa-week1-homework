package mutsa.delivery.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import mutsa.delivery.domain.CreditHistory;

import java.time.LocalDateTime;

public record CreditHistoryResponseDto(
        Long id,
        Long amount,
        String type,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static CreditHistoryResponseDto from(CreditHistory history) {
        return new CreditHistoryResponseDto(
                history.getId(),
                history.getAmount(),
                history.getType().name(),
                history.getCreatedAt()
        );
    }
}
