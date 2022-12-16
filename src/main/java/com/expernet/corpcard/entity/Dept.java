package com.expernet.corpcard.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "TB_DEPT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dept {

    @Id
    @Column(name = "seq", nullable = false)
    private long seq;

    @Column(name = "DEPT_NM", nullable = false)
    private String deptNm;

    @Column(name = "DEPT_CD", nullable = false)
    private String deptCd;

    @Column(name = "UPPER_DEPT_CD")
    private String upperDeptCd;

    @Column(name = "CHIEF_TITLE", nullable = false)
    private String chiefTitle;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public Dept(long seq, String deptNm, String deptCd, String upperDeptCd, String chiefTitle) {
        this.seq = seq;
        this.deptNm = deptNm;
        this.deptCd = deptCd;
        this.upperDeptCd = upperDeptCd;
        this.chiefTitle = chiefTitle;
    }
}
