package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "USER_ADD_INFO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAddInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name ="USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @Column(name = "USER_PASSWD")
    private String userPasswd;

    @Column(name = "MANAGER_YN",columnDefinition = "char")
    private String managerYn;

    @Builder
    public UserAddInfo(long seq, String userPasswd, String managerYn, User user) {
        this.seq = seq;
        this.userPasswd = userPasswd;
        this.managerYn = managerYn;
        this.user = user;
    }
}
