package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.user.UpdateUserRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "User API", description = "사용자 도메인 API (회원가입은 Auth API 참고)")
public interface UserApiDocs {

    @Operation(summary = "사용자 조회", description = "로그인한 사용자의 정보(크레딧 포함)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<UserResponseDto>> getMyInfo(Long userId);

    @Operation(summary = "사용자 정보 수정", description = "이름·비밀번호를 수정합니다. (전달된 값만 변경)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 정보 수정 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<UserResponseDto>> updateMyInfo(Long userId, UpdateUserRequestDto requestDto);

    @Operation(summary = "사용자 삭제", description = "로그인한 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 삭제 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<Void>> deleteUser(Long userId);
}
