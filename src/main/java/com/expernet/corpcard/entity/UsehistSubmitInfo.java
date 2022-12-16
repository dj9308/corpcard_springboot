package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "USEHIST_SUBMIT_INFO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsehistSubmitInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_SEQ")
    private StateInfo stateInfo;

    @Column(name = "WRITER_ID")
    private String writerId;

    @Column(name = "WRITER_DEPT")
    private String writerDept;

    @Column(name = "WRITER_OFCPS")
    private String writerOfcps;

    @Column(name = "WRITER_NM")
    private String writerNm;

    @Column(name = "WRT_YM")
    private Timestamp wrtYm;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public UsehistSubmitInfo(long seq, StateInfo stateInfo, String writerId, String writerDept, String writerOfcps,
                             String writerNm, Timestamp wrtYm){
        this.seq = seq;
        this.stateInfo = stateInfo;
        this.writerId = writerId;
        this.writerDept = writerDept;
        this.writerOfcps = writerOfcps;
        this.writerNm = writerNm;
        this.wrtYm = wrtYm;
    }
}
