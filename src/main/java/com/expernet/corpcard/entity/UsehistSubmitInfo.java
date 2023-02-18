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
public class UsehistSubmitInfo extends BaseEntity{
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

    @Column(name = "WRITER_TEAM")
    private String writerTeam;

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

    @Column(name = "REJECT_MSG")
    private String rejectMsg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private User user;

    @Builder
    public UsehistSubmitInfo(long seq, StateInfo stateInfo, String writerId, String writerDept,
                             String writerTeam, String writerOfcds, String writerNm, String wrtYm, String checkerId,
                             String checkerOfcds, String checkerNm, Timestamp approveDate){
        this.seq = seq;
        this.stateInfo = stateInfo;
        this.writerId = writerId;
        this.writerDept = writerDept;
        this.writerTeam = writerTeam;
        this.writerOfcds = writerOfcds;
        this.writerNm = writerNm;
        this.wrtYm = wrtYm;
        this.checkerId = checkerId;
        this.checkerOfcds = checkerOfcds;
        this.checkerNm = checkerNm;
        this.approveDate = approveDate;
    }
}
