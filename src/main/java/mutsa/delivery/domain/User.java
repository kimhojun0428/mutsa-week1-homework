package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.delivery.global.apiPayload.code.CreditErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.global.common.BaseTimeEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long credit;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.credit = 0L;
    }

    public static User create(String email, String password, String name) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    public void updateInfo(String name, String password) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }

    public void chargeCredit(long amount) {
        this.credit += amount;
    }

    public void useCredit(long amount) {
        if (this.credit < amount) {
            throw new ProjectException(CreditErrorCode.INSUFFICIENT_CREDIT);
        }
        this.credit -= amount;
    }
}
