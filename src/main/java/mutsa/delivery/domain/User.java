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

    // 소셜 회원은 비밀번호가 없으므로 nullable 이다.
    @Column
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long credit;

    // 가입 경로(LOCAL / KAKAO). 기존 회원은 모두 LOCAL 이다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType provider;

    // 소셜 공급자의 고유 식별자. LOCAL 회원은 null 이다.
    @Column
    private String providerId;

    // 소셜 프로필 이미지 URL. 없으면 null 이다.
    @Column
    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, String name,
                 SocialType provider, String providerId, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.credit = 0L;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
    }

    public static User create(String email, String password, String name) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .provider(SocialType.LOCAL)
                .build();
    }

    public static User createSocial(String email, String name, String profileImageUrl,
                                    SocialType provider, String providerId) {
        return User.builder()
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .provider(provider)
                .providerId(providerId)
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
