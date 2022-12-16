package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CLASS_INFO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "CLASS_CD")
    private String classCd;

    @Column(name = "CLASS_NM")
    private String classNm;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public ClassInfo(long seq, String classCd, String classNm){
        this.seq = seq;
        this.classCd = classCd;
        this.classNm = classNm;
    }
}
