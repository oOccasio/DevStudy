package chuchuchi.chuchuchi.domain.member.dto;

import chuchuchi.chuchuchi.domain.member.Member;

public record MemberSignUpDto (String username, String password,
                               String name, String nickname, Integer age){

    public Member toEntity(){
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .nickName(nickname)
                .age(age).build();

    }

}
