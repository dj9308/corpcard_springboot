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
public class StateInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "STATE_CD")
    private String stateCd;

    @Column(name = "STATE_NM")
    private String stateNm;

    @Builder
    public StateInfo(long seq, String stateCd, String stateNm){
        this.seq = seq;
        this.stateCd = stateCd;
        this.stateNm = stateNm;
    }
}
