package chuchuchi.chuchuchi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberWithDrawDto(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String checkPassword
) {
}
