package chuchuchi.chuchuchi.domain.member.dto;

import chuchuchi.chuchuchi.domain.member.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDto {

    private String name;
    private String nickname;
    private String username;
    private Integer age;

    @Builder
    public MemberInfoDto(Member member) {
        this.name = member.getName();
        this.nickname = member.getNickName();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}
