package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "USEHIST_SUBMIT_INFO")
@NoArgsConstructor
public class UsehistSubmitInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATE_SEQ")
    private StateInfo stateInfo;

    @Column(name = "WRITER_ID")
    private String writerId;

    @Column(name = "WRITER_DEPT")
    private String writerDept;

    @Column(name = "WRITER_OFCDS")
    private String writerOfcds;

    @Column(name = "WRITER_NM")
    private String writerNm;

    @Column(name = "WRT_YM")
    private String wrtYm;

    @Column(name = "CHECKER_ID")
    private String checkerId;

    @Column(name = "CHECKER_OFCDS")
    private String checkerOfcds;

    @Column(name = "CHECKER_NM")
    private String checkerNm;

    @Column(name = "APPROVE_DATE")
    private Timestamp approveDate;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public UsehistSubmitInfo(long seq, StateInfo stateInfo, String writerId, String writerDept, String writerOfcds,
                             String writerNm, String wrtYm, String checkerId, String checkerOfcds, String checkerNm,
                            Timestamp approveDate){
        this.seq = seq;
        this.stateInfo = stateInfo;
        this.writerId = writerId;
        this.writerDept = writerDept;
        this.writerOfcds = writerOfcds;
        this.writerNm = writerNm;
        this.wrtYm = wrtYm;
        this.checkerId = checkerId;
        this.checkerOfcds = checkerOfcds;
        this.checkerNm = checkerNm;
        this.approveDate = approveDate;
    }
}
