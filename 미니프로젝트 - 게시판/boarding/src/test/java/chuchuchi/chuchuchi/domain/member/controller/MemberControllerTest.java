package chuchuchi.chuchuchi.domain.member.controller;

import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();

    private static String SIGN_UP_URL = "/signUp";

    private String username = "username";
    private String password = "password1234@";
    private String name = "shinD";
    private String nickName = "shinD cute";
    private Integer age = 22;


    private void clear(){
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpData))
                .andExpect(status().isOk());

    }

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

        Map<String , String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        MvcResult result = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }


    @Test
    public void signUpSuccess() throws Exception {
        //given
        String signUpData = objectMapper
                .writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));

        //when
        signUp(signUpData);

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(member.getName()).isEqualTo(name);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }


    @Test
    public void signUpFailForEmptyField() throws Exception {
        //given
        String noUsernameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(null, password, name, nickName, age));
        String noPasswordSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, null, name, nickName, age));
        String noNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, null, nickName, age));
        String noNickNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, null, age));
        String noAgeSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, null));

        //when, then
        signUp(noUsernameSignUpData);
        signUp(noPasswordSignUpData);
        signUp(noNameSignUpData);
        signUp(noNickNameSignUpData);
        signUp(noAgeSignUpData);
        em.clear();

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void updateInfoSuccess() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));

        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name+"변경");
        map.put("nickname", nickName+"변경");
        map.put("age", age+1);
        String updateMemberData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                put("/member")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception ("회원이 없습니다."));
        assertThat(member.getName()).isEqualTo(name+"변경");
        assertThat(member.getNickName()).isEqualTo(nickName+"변경");
        assertThat(member.getAge()).isEqualTo(age+1);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void updatePasswordSuccess() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password + "!@#@!#@!#");

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                put("/member/password")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password + "!@#@!#@!#", member.getPassword())).isTrue();

    }

    @Test
    public void updatePasswordFailForNotMatchPassword() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password + "1");
        map.put("toBePassword", password + "!@#@!#@!#");

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password + "!@#@!#@!#", member.getPassword())).isFalse();

    }

    @Test
    public void updatePasswordFailForNoValidPasswordForm() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", "123123");

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123123", member.getPassword())).isFalse();

    }


    @Test
    public void memberWithDrawSuccess() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                delete("/member")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword))
                .andExpect(status().isOk());

        //then
        assertThrows(Exception.class, () -> memberRepository.findByUsername(username).orElseThrow(() -> new Exception ("회원이 없습니다.")));
    }


    @Test
    public void memberWithDrawFailForInvalidPassword() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password + 11);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                delete("/member")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(member).isNotNull();
    }

    @Test
    public void memberWithDrawFailForNoAuthorization () throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER+accessToken + '1')
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isForbidden());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(member).isNotNull();
    }

    @Test
    public void getMyInfoSuccess() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        //when
        MvcResult result = mockMvc.perform(
                get("/member")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));
        assertThat(member.getAge()).isEqualTo(map.get("age"));
        assertThat(member.getName()).isEqualTo(map.get("name"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getNickName()).isEqualTo(map.get("nickname"));
    }


    @Test
    public void getMyInfoFailForNoJWT() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        //when, then
        mockMvc.perform(
                get("/member")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());

    }

    @Test
    public void getInfoSuccess() throws Exception {

        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = memberRepository.findAll().get(0).getId();


        //when
        MvcResult result = mockMvc.perform(
                        get("/member/"+id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));
        assertThat(member.getAge()).isEqualTo(map.get("age"));
        assertThat(member.getName()).isEqualTo(map.get("name"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getNickName()).isEqualTo(map.get("nickname"));

    }

    @Test
    public void getInfoFailForNoId() throws Exception {

        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when
        MvcResult result = mockMvc.perform(
                        get("/member/2211")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        assertThat(result.getResponse().getContentAsString()).isEqualTo("");

    }

    @Test
    public void getInfoFailForNoJWT() throws Exception {

        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, name, nickName, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = memberRepository.findAll().get(0).getId();

        //when,then
        mockMvc.perform(
                get("/member/"+id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isForbidden());


    }
}
