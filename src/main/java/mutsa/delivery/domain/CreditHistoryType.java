package mutsa.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreditHistoryType {

    CHARGE("충전"),
    USE("사용"),
    REFUND("환불");

    private final String description;

    /** 잔액이 늘어나는 유형인지 (CHARGE·REFUND) 여부 */
    public boolean isIncrease() {
        return this == CHARGE || this == REFUND;
    }
}
