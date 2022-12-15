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

    @Column(name = "STATUS_SEQ")
    private long statusSeq;

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

    @Builder
    public UsehistSubmitInfo(long seq, long statusSeq, String writerId, String writerDept, String writerOfcps,
                             String writerNm, Timestamp wrtYm){
        this.seq = seq;
        this.statusSeq = statusSeq;
        this.writerId = writerId;
        this.writerDept = writerDept;
        this.writerOfcps = writerOfcps;
        this.writerNm = writerNm;
        this.wrtYm = wrtYm;
    }
}
