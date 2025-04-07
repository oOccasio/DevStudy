package chuchuchi.chuchuchi.global.login.filter;


import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.global.jwt.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class JwtFilterAuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static  String KEY_USERNAME = "username";
    private static  String KEY_PASSWORD = "password";
    private static  String USERNAME = "username";
    private static  String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";
    private static String LOGIN_FAIL_MESSAGE = "fail";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";

    private ObjectMapper objectMapper = new ObjectMapper();


    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build());

        clear();
    }

    private Map<String, String> getUsernamePasswordMap(String username, String password){
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private Map<String, String> getAccessAndRefreshToken() throws Exception{

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }

    @Test
    public void Access_Refresh_Token_IsEmpty() throws Exception {
        //when, //then
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void Only_AccessToken_Send() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        //when,then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL+"123").header(accessHeader, BEARER + accessToken))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void No_Valid_Only_AccessToken_Send_Status_403() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL+"123").header(accessHeader, accessToken + "1"))
                        .andExpectAll(status().isForbidden());
    }

    @Test
    public void Valid_RefreshToken_Send_AccessToken_RE_Status_200() throws Exception {


        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();

        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL + "123").header(refreshHeader, BEARER + refreshToken))
                .andExpect(status().isOk()).andReturn();


        String accessToken = result.getResponse().getHeader(accessHeader);
        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);

    }

    @Test
    public void No_Valid_RefreshToken_Send_Status_403() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL + "123").header(refreshHeader, refreshToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(LOGIN_RUL + "123").header(refreshToken, BEARER + refreshToken + "1"))
                .andExpect(status().isForbidden());

    }

    @Test
    public void Valid_RefreshToken_And_Valid_AccessToken_Send_AccessToken_RE_Status_200() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                .header(refreshHeader, BEARER + refreshToken)
                .header(accessToken, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    public void Valid_RefreshToken_No_Valid_AccessToken_Send_RE_Status_200() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL+"123")
                .header(refreshHeader, BEARER + refreshToken)
                .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();

    }

    @Test
    public void No_Valid_RefreshToken_And_Valid_AccessToken_Send_RE_Status_200_OR_Status_403_RE_X() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when,then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                .header(refreshHeader, BEARER + refreshToken + 1)
                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();

    }

    @Test
    public void No_Valid_RefreshToken_And_No_Valid_AccessToken_Send_Status_403() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                .header(refreshToken,BEARER + refreshToken + 1)
                .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isForbidden())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();

    }

    @Test
    public void LoginAddress_Send_Filter_Not_Operate() throws Exception {
        //given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(post(LOGIN_RUL)
                .header(refreshToken, BEARER + refreshToken)
                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(LOGIN_FAIL_MESSAGE);

    }

}
