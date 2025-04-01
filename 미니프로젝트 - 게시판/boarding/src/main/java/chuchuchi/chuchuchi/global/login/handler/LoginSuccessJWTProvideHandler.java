package chuchuchi.chuchuchi.global.login.handler;

import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.global.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        memberRepository.findByUsername(username).ifPresent(
                member -> member.updateRefreshToken(refreshToken)
        );

        log.info("로그인에 성공합니다. username: {}", username);
        log.info("AccessToken 을 발급합니다. AccessToken: {}", accessToken);
        log.info("RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }
}
