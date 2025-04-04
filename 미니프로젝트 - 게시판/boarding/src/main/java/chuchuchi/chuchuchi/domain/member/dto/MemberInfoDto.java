package chuchuchi.chuchuchi.domain.member.dto;

import chuchuchi.chuchuchi.domain.member.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberInfoDto {
    private final String name;
    private final String nickname;
    private final String username;
    private final Integer age;

    @Builder
    public MemberInfoDto(Member member) {
        this.name = member.getName();
        this.nickname = member.getNickName();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}
