package com.expernet.corpcard.entity;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "TB_DEPT")
@NoArgsConstructor
public class Dept extends BaseEntity{

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

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPPER_DEPT_CD", referencedColumnName = "DEPT_CD", insertable = false, updatable = false)
    private Dept upper;

    @JsonManagedReference
    @OneToMany(mappedBy = "upper", fetch = FetchType.LAZY)
    private final List<Dept> lower = new ArrayList<>();

    @Builder
    public Dept(long seq, String deptNm, String deptCd, String upperDeptCd, String chiefTitle) {
        this.seq = seq;
        this.deptNm = deptNm;
        this.deptCd = deptCd;
        this.upperDeptCd = upperDeptCd;
        this.chiefTitle = chiefTitle;
    }
}
