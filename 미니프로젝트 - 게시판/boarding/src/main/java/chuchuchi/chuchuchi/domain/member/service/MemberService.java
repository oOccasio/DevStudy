package chuchuchi.chuchuchi.domain.member.service;

import chuchuchi.chuchuchi.domain.member.dto.MemberInfoDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberUpdateDto;

public interface MemberService {

    /*
    회원가입
    정보수정
    회원탈퇴
    정보조회
     */

    void signUp(MemberSignUpDto memberSignUpDto) throws Exception;

    void update(MemberUpdateDto memberUpdateDto) throws Exception;

    void updatePassword(String checkPassword, String toBePassword) throws Exception;

    void withdraw(String checkPassword) throws Exception;

    MemberInfoDto getInfo(Long id) throws Exception;

    MemberInfoDto getMyInfo() throws Exception;
}
