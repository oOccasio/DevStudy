package chuchuchi.chuchuchi.domain.member.service;

import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.dto.MemberInfoDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberUpdateDto;
import chuchuchi.chuchuchi.domain.member.exception.MemberException;
import chuchuchi.chuchuchi.domain.member.exception.MemberExceptionType;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "password";

    private void clear(){
        em.flush();
        em.clear();
    }

    private MemberSignUpDto makeMemberSignUpDto(){
        return new MemberSignUpDto("username", PASSWORD, "name", "nickname", 22);
    }

    private MemberSignUpDto setMember() throws Exception {
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(memberSignUpDto.username())
                .password(memberSignUpDto.password())
                .roles(Role.USER.name())
                .build(), null, null));

        SecurityContextHolder.setContext(emptyContext);
        return memberSignUpDto;
    }

    @AfterEach
    public void removeMember(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    /*
    회원가입
    회원가입 시 아이디, 비밀번호, 이름, 별명, 나이를 입력하지 않으면 오류
    이미 존재하는 아이디가 있을시 오류
    회원가입 후 회원의 ROLE은 USER
    */
    @Test
    public void signUpSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();

        //when
        memberService.signUp(memberSignUpDto);
        clear();

        //then
        Member member = memberRepository.findByUsername(memberSignUpDto.username())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickname());
        assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
        assertThat(member.getRole()).isEqualTo(Role.USER);

    }

    @Test
    public void signUpFailForDuplicateUsername() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();
        //when, then
        assertThat(assertThrows(MemberException.class, () -> memberService.signUp(memberSignUpDto))
                .getBaseExceptionType()).isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);
}

    @Test
    public void singUpFailForEmptyFields() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto1 = new MemberSignUpDto(null, passwordEncoder.encode(PASSWORD), "name", "nickname", 22);
        MemberSignUpDto memberSignUpDto2 = new MemberSignUpDto("username", null, "name", "nickname", 22);
        MemberSignUpDto memberSignUpDto3 = new MemberSignUpDto("username", passwordEncoder.encode(PASSWORD), null, "nickname", 22);
        MemberSignUpDto memberSignUpDto4 = new MemberSignUpDto("username", passwordEncoder.encode(PASSWORD), "name", null, 22);
        MemberSignUpDto memberSignUpDto5 = new MemberSignUpDto("username", passwordEncoder.encode(PASSWORD), "name", "nickname", null);

        //when, then

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto1));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto2));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto3));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto4));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto5));

    }

    /*
    회원 정보수정
    회원가입을 하지 않은 사람이 정보수정시 오류 -> 시큐리티 필터가 방지
    아이디는 변경 불가능
    비밀번호 변경시에는 현재 비밀번호를 입력받아 일치한 경우에만 바꿈
    비밀번호 변경시에는 오직 비밀번호만 변경

    비밀번호가 아닌 이름, 별명, 나이는 3개를 한번에 변경도 가능이고 한 두개만 변경가능
    하지만 아무것도 안바뀌면 오류
     */

    @Test
    public void updatePasswordSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String toBePassword = "123456789!@#!@#";
        memberService.updatePassword(PASSWORD, toBePassword);
        clear();

        //then
        Member findMember = memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new Exception());
        assertThat(findMember.matchPassword(passwordEncoder, toBePassword)).isTrue();

    }

    @Test
    public void updateNameSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String updateName = "updateName";
        memberService.update(new MemberUpdateDto(Optional.of(updateName), Optional.empty(), Optional.empty()));
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getName()).isEqualTo(updateName);
            assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickname());
            assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
        }));
    }

    @Test
    public void updateNickNameSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String updateNickName = "updateNickName";
        memberService.update(new MemberUpdateDto(Optional.empty(), Optional.of(updateNickName), Optional.empty()));
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
            assertThat(member.getNickName()).isEqualTo(updateNickName);
            assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
        }));
    }

    @Test
    public void updateAgeSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        Integer updateAge = 33;
        memberService.update(new MemberUpdateDto(Optional.empty(), Optional.empty(), Optional.of(updateAge)));
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickname());
            assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        }));

    }

    /*
    회원탈퇴
    비밀번호를 입력받아서 일치하면 탈퇴가능
     */
    @Test
    public void deleteUserSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        memberService.withdraw(PASSWORD);

        //then
        assertThat(assertThrows(Exception.class, () -> memberRepository.findByUsername(memberSignUpDto.username())
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다."))).getMessage())
                .isEqualTo("회원이 존재하지 않습니다.");
    }

    @Test
    public void deleteUserFailForNoMatchPassword() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        assertThat(assertThrows(MemberException.class, () -> memberService.withdraw(PASSWORD+"123"))
                .getBaseExceptionType()).isEqualTo(MemberExceptionType.WRONG_PASSWORD);
    }

    @Test
    public void getInfoSuccess() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();
        Member member = memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new Exception());

        //when
        MemberInfoDto info = memberService.getInfo(member.getId());

        //then
        assertThat(info.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(info.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(info.getNickname()).isEqualTo(memberSignUpDto.nickname());
        assertThat(info.getAge()).isEqualTo(memberSignUpDto.age());

    }

    @Test
    public void getMyInfoSuccess() throws Exception{
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        MemberInfoDto myInfo = memberService.getMyInfo();

        //then
        assertThat(myInfo.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(myInfo.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(myInfo.getNickname()).isEqualTo(memberSignUpDto.nickname());
        assertThat(myInfo.getAge()).isEqualTo(memberSignUpDto.age());

    }

}



