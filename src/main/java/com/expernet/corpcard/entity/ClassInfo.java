package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CLASS_INFO")
@NoArgsConstructor
public class ClassInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "CLASS_CD")
    private String classCd;

    @Column(name = "CLASS_NM")
    private String classNm;

    @Builder
    public ClassInfo(long seq, String classCd, String classNm){
        this.seq = seq;
        this.classCd = classCd;
        this.classNm = classNm;
    }
}
