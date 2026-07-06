package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.UserApiDocs;
import mutsa.delivery.dto.user.SignUpRequestDto;
import mutsa.delivery.dto.user.UpdateUserRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApiDocs {

    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<GlobalResponse<UserResponseDto>> signUp(
            @Valid
            @RequestBody SignUpRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.signUp(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<UserResponseDto>> getMyInfo(
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserResponseDto responseDto = userService.getMyInfo(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @PatchMapping
    public ResponseEntity<GlobalResponse<UserResponseDto>> updateMyInfo(
            @RequestHeader("X-User-Id") Long userId,
            @Valid
            @RequestBody UpdateUserRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.updateMyInfo(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<GlobalResponse<Void>> deleteUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        userService.deleteUser(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess());
    }
}
