package mutsa.delivery.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateAddressRequestDto(

        @NotBlank(message = "주소명을 입력해주세요.")
        String addressName,

        @NotBlank(message = "주소지를 입력해주세요.")
        String address,

        @NotBlank(message = "우편번호를 입력해주세요.")
        @Pattern(regexp = "^[0-9]{5}$", message = "한국 우편번호 기준 5자리 숫자여야 합니다.")
        String zipCode,

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phoneNumber

) {}
