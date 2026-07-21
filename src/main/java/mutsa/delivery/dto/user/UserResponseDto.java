package mutsa.delivery.dto.user;

import mutsa.delivery.domain.SocialType;
import mutsa.delivery.domain.User;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Long credit,
        SocialType provider,
        String profileImageUrl
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCredit(),
                user.getProvider(),
                user.getProfileImageUrl()
        );
    }
}
