package mutsa.delivery.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDERED("주문 완료"),
    PREPARING("조리 중"),
    DELIVERING("배달 중"),
    DELIVERED("배달 완료"),
    CANCELED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
