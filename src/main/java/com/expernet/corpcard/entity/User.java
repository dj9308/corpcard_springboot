package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity
@Table(name = "TB_USER")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "USER_ID")
    private String userId;

    @OneToOne(mappedBy = "user")
    private UserAddInfo userAddInfo;

    @Column(name = "OFCDS")
    private String ofcds;

    @Column(name = "USER_NM")
    private String userNm;

    @Column(name = "CHIEF_YN")
    private Character chiefYn;

    @Column(name = "DEPT_CD")
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
