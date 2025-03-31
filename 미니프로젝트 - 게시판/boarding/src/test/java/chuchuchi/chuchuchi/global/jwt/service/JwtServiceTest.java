package chuchuchi.chuchuchi.global.jwt.service;

import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.global.jwt.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class JwtServiceTest {


    @Autowired
    JwtService jwtService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer";

    private String username = "username";
    @Autowired
    private HttpServletRequest httpServletRequest;

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;


    }

    public void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        Member member = Member.builder()
                .username(username)
                .password("123456789")
                .name("Member1")
                .nickName("Nickname1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(member);
        clear();
    }

    private DecodedJWT getVerify(String token){
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    public void createAccessToken_AccessToken() throws Exception{

        //given
        String accessToken = jwtService.createAccessToken(username);

        DecodedJWT verify = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken);
        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        assertThat(findUsername).isEqualTo(username);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void createRefreshToken_RefreshToken() throws Exception{
        //given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = JWT.require(Algorithm.HMAC512(secret)).build().verify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();

        //then
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(username).isNull();
    }

    @Test
    public void updateRefreshToken_RefreshToken() throws Exception{
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);

        //when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        //then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getUsername()).isEqualTo(username);
    }

    @Test
    public void destroyRefreshToken_RefreshToken() throws Exception{
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        //when
        jwtService.destroyRefreshToken(username);
        clear();

        //then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());

        Member member = memberRepository.findByUsername(username).get();
        assertThat(member.getRefreshToken()).isNull();
    }

    @Test
    public void Token_Access_Valid() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when,then
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken + "d")).isFalse();
        assertThat(jwtService.isTokenValid(refreshToken + "d")).isFalse();
    }


    @Test
    public void setAccessTokenHeader_AccessToken() throws Exception{
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);

    }

    @Test
    public void setRefreshTokenHeader_RefreshToken() throws Exception{
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerRefreshToken).isEqualTo(refreshToken);

    }

    @Test
    public void sendAccessAndRefreshToken() throws Exception{
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void extractAccessToken_AccessToken () throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        HttpServletRequest httpservletRequest = setRequest(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(httpservletRequest)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }

    @Test
    public void extractRefreshToken_RefreshToken () throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        //when
        String extractRefreshToken = jwtService.extractRefreshToken(request)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);

    }

    @Test
    public void extractUsername_Username() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletrequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletrequest)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //when
        String extractUsername = jwtService.extractUsername(requestAccessToken)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractUsername).isEqualTo(username);
    }

}
