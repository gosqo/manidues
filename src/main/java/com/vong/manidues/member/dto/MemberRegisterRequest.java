package com.vong.manidues.member.dto;

import com.vong.manidues.member.Member;
import com.vong.manidues.member.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class MemberRegisterRequest {

    @NotBlank(message = "Email 을 입력해주세요.")
    @Pattern(
            regexp =
                    "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$",
            message = "올바른 형식의 Email 을 입력해주세요."
    )
    @Size(
            min = 6
            , max = 50
            , message = "이메일은 6 ~ 50 자리로 입력해주세요."
    )
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp =
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+{}|:\"<>?`=\\[\\]\\-_\\\\;',./]{8,20}$",
            message = "비밀번호 조합에 영문 대소문자/숫자가 최소 하나씩은 필요합니다."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(
            min = 2
            , max = 20
    )
    private String nickname;

    public Member toEntity(String password) {
        return Member.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .role(Role.USER)
                .build();
    }

}
