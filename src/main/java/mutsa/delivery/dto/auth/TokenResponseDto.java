package mutsa.delivery.dto.auth;

public record TokenResponseDto(
        String accessToken,
        String tokenType
) {
    private static final String BEARER = "Bearer";

    public static TokenResponseDto of(String accessToken) {
        return new TokenResponseDto(accessToken, BEARER);
    }
}
