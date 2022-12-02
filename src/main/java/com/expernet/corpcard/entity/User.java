package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "tb_user")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "seq", nullable = false)
    private long seq;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "ofcds", nullable = false)
    private String ofcds;

    @Column(name = "user_nm", nullable = false)
    private String userNm;

    @Column(name = "chief_yn", nullable = false)
    private Character chiefYn;

    @Column(name = "dept_cd", nullable = false)
    private String deptCd;

    @Builder
    public User(long seq, String userId, String ofcds, String userNm, Character chiefYn, String deptCd) {
        this.seq = seq;
        this.userId = userId;
        this.ofcds = ofcds;
        this.userNm = userNm;
        this.chiefYn = chiefYn;
        this.deptCd = deptCd;
    }
}
