package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "STATE_INFO")
@NoArgsConstructor
public class StateInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "STATE_CD")
    private String classCd;

    @Column(name = "STATE_NM")
    private String classNm;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public StateInfo(long seq, String classCd, String classNm){
        this.seq = seq;
        this.classCd = classCd;
        this.classNm = classNm;
    }
}
