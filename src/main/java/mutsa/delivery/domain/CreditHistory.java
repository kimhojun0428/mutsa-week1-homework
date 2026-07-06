package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.delivery.global.common.BaseTimeEntity;

@Entity
@Table(name = "credit_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditHistoryType type;

    @Builder(access = AccessLevel.PRIVATE)
    private CreditHistory(User user, Long amount, CreditHistoryType type) {
        this.user = user;
        this.amount = amount;
        this.type = type;
    }

    public static CreditHistory of(User user, Long amount, CreditHistoryType type) {
        return CreditHistory.builder()
                .user(user)
                .amount(amount)
                .type(type)
                .build();
    }
}
