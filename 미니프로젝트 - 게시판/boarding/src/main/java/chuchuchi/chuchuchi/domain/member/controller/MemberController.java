package chuchuchi.chuchuchi.domain.member.controller;

import chuchuchi.chuchuchi.domain.member.dto.*;
import chuchuchi.chuchuchi.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //비밀번호 수정
    @PutMapping("/member/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
        memberService.updatePassword(updatePasswordDto.checkPassword(), updatePasswordDto.toBePassword());
    }

    //회원탈퇴
    @DeleteMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody MemberWithDrawDto memberWithDrawDto) throws Exception {
        memberService.withdraw(memberWithDrawDto.checkPassword());
    }

    //회원 정보 조회
    @GetMapping("/member/{id}")
    public ResponseEntity<MemberInfoDto> getInfo (@Valid @PathVariable("id") Long id) throws Exception {
        MemberInfoDto info = memberService.getInfo(id);
        return ResponseEntity.ok(info);
    }

    //내정보 조회
    @GetMapping("/member")
    public ResponseEntity <MemberInfoDto> getMyInfo (HttpServletResponse response) throws Exception {

        MemberInfoDto info = memberService.getMyInfo();
        return ResponseEntity.ok(info);
    }


}
