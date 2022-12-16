package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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

    @Column(name = "ADMIN_YN",columnDefinition = "char")
    private String adminYn;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public UserAddInfo(long seq, String userPasswd, String adminYn, User user) {
        this.seq = seq;
        this.userPasswd = userPasswd;
        this.adminYn = adminYn;
        this.user = user;
    }
}
