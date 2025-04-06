package chuchuchi.chuchuchi.domain.member.service;

import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.dto.MemberInfoDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberUpdateDto;
import chuchuchi.chuchuchi.domain.member.exception.MemberException;
import chuchuchi.chuchuchi.domain.member.exception.MemberExceptionType;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

        Member member = memberSignUpDto.toEntity();
        member.addUserAuthority();
        member.encodePassword(passwordEncoder);

        if(memberRepository.findByUsername(memberSignUpDto.username()).isPresent()){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }

        memberRepository.save(member);

    }

    @Override
    public void update(MemberUpdateDto memberUpdateDto) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        memberUpdateDto.name().ifPresent(member::updateName);
        memberUpdateDto.nickname().ifPresent(member::updateNickName);
        memberUpdateDto.age().ifPresent(member::updateAge);
    }

    @Override
    public void updatePassword(String checkPassword, String toBePassword) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder, toBePassword);

    }

    @Override
    public void withdraw(String checkPassword) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);

    }

    @Override
    public MemberInfoDto getInfo(Long id) throws Exception {
        Member member = memberRepository.findById(id)
                .orElseThrow(()-> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        return new MemberInfoDto(member);
    }

    @Override
    public MemberInfoDto getMyInfo() throws Exception {

        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(()-> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        return new MemberInfoDto(member);
    }
}
