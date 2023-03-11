package com.expernet.corpcard.dto.login;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String userId;
    private String userPasswd;
}
