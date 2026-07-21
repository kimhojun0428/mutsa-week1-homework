package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.auth.LoginRequestDto;
import mutsa.delivery.dto.auth.TokenResponseDto;
import mutsa.delivery.dto.user.SignUpRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth API", description = "회원가입·로그인 API (인증 불필요)")
public interface AuthApiDocs {

    @Operation(
            summary = "회원가입",
            description = "이메일·비밀번호·이름으로 회원가입합니다. 비밀번호는 암호화되어 저장됩니다."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수값 누락·형식 오류(INVALID_REQUEST)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복(USER_409_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<UserResponseDto>> signUp(SignUpRequestDto requestDto);

    @Operation(
            summary = "로그인",
            description = "이메일·비밀번호를 검증하고 JWT Access Token을 발급합니다."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "401",
                    description = "존재하지 않는 사용자 또는 비밀번호 불일치(LOGIN_FAILED)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<TokenResponseDto>> login(LoginRequestDto requestDto);

    @Operation(
            summary = "로그아웃",
            description = "클라이언트가 저장한 Access Token을 삭제하도록 하는 무상태 로그아웃입니다. "
                    + "서버는 별도 세션·토큰 저장소가 없어 상태를 변경하지 않으며 항상 성공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공(COMMON_200_1)")
    })
    ResponseEntity<GlobalResponse<Void>> logout(@Parameter(hidden = true) Long userId);
}
