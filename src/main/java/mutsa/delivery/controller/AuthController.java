package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.AuthApiDocs;
import mutsa.delivery.dto.auth.LoginRequestDto;
import mutsa.delivery.dto.auth.TokenResponseDto;
import mutsa.delivery.dto.user.SignUpRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.config.security.AuthUser;
import mutsa.delivery.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<GlobalResponse<UserResponseDto>> signUp(
            @Valid
            @RequestBody SignUpRequestDto requestDto
    ) {
        UserResponseDto responseDto = authService.signUp(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<TokenResponseDto>> login(
            @Valid
            @RequestBody LoginRequestDto requestDto
    ) {
        TokenResponseDto responseDto = authService.login(requestDto);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse<Void>> logout(
            @AuthUser Long userId
    ) {
        authService.logout(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess());
    }
}
