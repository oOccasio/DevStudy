package chuchuchi.chuchuchi.domain.member.controller;

import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberUpdateDto;
import chuchuchi.chuchuchi.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //회원가입
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        memberService.signUp(memberSignUpDto);
    }

    //회원정보수정
    @PutMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public void updateBasicInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) throws Exception {
        memberService.update(memberUpdateDto);
    }

    @PutMapping("/member/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePa) throws Exception {}

}
