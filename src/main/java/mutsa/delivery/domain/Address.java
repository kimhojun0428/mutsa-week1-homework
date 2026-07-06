package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.global.common.BaseTimeEntity;

@Entity
@Table(name = "addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressName;
    private String address;
    private String zipCode;
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder(access = AccessLevel.PRIVATE)
    private Address(
            User user,
            String addressName,
            String address,
            String zipCode,
            String phoneNumber
    ) {
        this.user = user;
        this.addressName = addressName;
        this.address = address;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
    }

    public static Address create(
            User user,
            String addressName,
            String address,
            String zipCode,
            String phoneNumber
    ) {
        return Address.builder()
                .user(user)
                .addressName(addressName)
                .address(address)
                .zipCode(zipCode)
                .phoneNumber(phoneNumber)
                .build();
    }

    public void validateUser(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new ProjectException(UserErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    public void updateAddress(
            String addressName,
            String address,
            String zipCode,
            String phoneNumber
    ) {
        this.addressName = addressName;
        this.address = address;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
    }
}
