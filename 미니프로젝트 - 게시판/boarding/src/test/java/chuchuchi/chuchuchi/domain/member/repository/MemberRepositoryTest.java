package chuchuchi.chuchuchi.domain.member.repository;

import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @AfterEach
    public void after(){
        em.clear();
    }


    @Test
    public void user_save() throws Exception {

        //given
        Member member = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        //when
        Member saveMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(saveMember.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));

        assertThat(findMember).isSameAs(saveMember);
        assertThat(findMember).isSameAs(member);
    }

    @Test
    public void user_save_No_Id() throws Exception {

        //given
        Member member = Member.builder()
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));

    }

    @Test
    public void user_save_no_name() throws Exception {

        //given
        Member member = Member.builder()
                .username("username")
                .password("1234567890")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }


    @Test
    public void user_save_no_nickName() throws Exception {

        //given
        Member member = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .role(Role.USER)
                .age(22)
                .build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));

    }

    @Test
    public void user_save_no_age() throws Exception {

        //given
        Member member = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }

    @Test
    public void duplicate_user() throws Exception {

        //given
        Member member1 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        Member member2 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member1);
        em.clear();

        //when,then
        assertThrows(Exception.class, () -> memberRepository.save(member2));

    }

    @Test
    public void modify_user() throws Exception {

        //given
        Member member1 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member1);

        String updatePassword = "updatePassword";
        String updateName = "updateName";
        String updateNickName = "updateNickName";
        int updateAge = 33;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        em.clear();

        //when
        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(Exception::new);
        findMember.updateName(updateName);
        findMember.updateNickName(updateNickName);
        findMember.updateAge(updateAge);
        findMember.updatePassword(passwordEncoder, updatePassword);
        em.flush();

        //then

        Member findUpdateMember = memberRepository.findById(member1.getId()).orElseThrow(Exception::new);

        assertThat(findUpdateMember).isSameAs(findMember);
        assertThat(passwordEncoder.matches(updatePassword, findUpdateMember.getPassword())).isTrue();
        assertThat(findUpdateMember.getName()).isEqualTo(updateName);
        assertThat(findUpdateMember.getName()).isNotEqualTo(member1.getName());

    }


    @Test
    public void existsByUsername() throws Exception {

        //given
        Member member1 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member1);

        //When, Then
        assertThat(memberRepository.existsByUsername("username")).isTrue();

    }

    @Test
    public void delete_user() throws Exception {

        Member member1 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member1);
        em.clear();

        //when
        memberRepository.delete(member1);
        em.flush();
        em.clear();


        //then
        assertThrows(Exception.class, () -> memberRepository.findById(member1.getId()).orElseThrow(Exception::new));


    }

    @Test
    public void findByUsername() throws Exception {

        //given
        String username = "username";
        Member member1 = Member.builder()
                .username(username)
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member1);
        em.clear();

        //when, then

        assertThat(memberRepository.findByUsername(username).get().getUsername()).isEqualTo(member1.getUsername());
        assertThat(memberRepository.findByUsername(username).get().getName()).isEqualTo(member1.getName());
        assertThat(memberRepository.findByUsername(username).get().getId()).isEqualTo(member1.getId());
        assertThrows(Exception.class, () -> memberRepository.findByUsername(username+"123").orElseThrow(Exception::new));

    }

    @Test
    public void saveUser_Report_Time(){

        //given
        Member member1 = Member.builder()
                .username("username")
                .password("1234567890")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();
        memberRepository.save(member1);
        em.clear();

        //when
        Member findMember = memberRepository.findById(member1.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        //then
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getLastModifiedDate()).isNotNull();
    }


}