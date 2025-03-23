package chuchuchi.chuchuchi.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void Password_Hashing() throws Exception {

        //given
        String password = "신동훈ShinDongHun";

        //when
        String encodePassword = passwordEncoder.encode(password);

        //then
        assertThat(encodePassword).startsWith("{");
        assertThat(encodePassword).contains("{bcrypt}");
        assertThat(encodePassword).isNotEqualTo(password);
    }

    @Test
    public void Random_Hashing() throws Exception {

        //given
        String password = "신동훈ShinDongHun";

        //when
        String encodePassword1 = passwordEncoder.encode(password);
        String encodePassword2 = passwordEncoder.encode(password);

        //then
        assertThat(encodePassword1).isNotEqualTo(encodePassword2);
    }

    @Test
    public void Password_Match() throws Exception {

        //given
        String password = "신동훈ShinDongHun";

        //when
        String encodePassword = passwordEncoder.encode(password);

        //then
        assertThat(passwordEncoder.matches(password, encodePassword)).isTrue();

    }
}
