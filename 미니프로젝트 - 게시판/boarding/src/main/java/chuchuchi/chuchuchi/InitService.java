package chuchuchi.chuchuchi;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class InitService {


    private final Init init;


    @PostConstruct
    @Transactional
    public void init() {

        if(init.hasNoMember()){
            init.save();
        }

    }
}
