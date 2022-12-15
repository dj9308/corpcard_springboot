package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "CARD_USEHIST")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUsehist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "SUBMIT_SEQ")
    private UsehistSubmitInfo usehistSubmitInfo;

    @Column(name = "CLASS_SEQ")
    private long classSeq;

    @Column(name = "USE_PLACE")
    private String usePlace;

    @Column(name = "CARD_COMP")
    private String cardComp;

    @Column(name = "CARD_NUM")
    private String cardNum;

    @Column(name = "USE_DATE")
    private Timestamp useDate;

    @Column(name = "MONEY")
    private long money;

    @Builder
    public CardUsehist(long seq, long classSeq, String usePlace, String cardComp, String cardNum,
                       Timestamp useDate, long money, UsehistSubmitInfo usehistSubmitInfo){
        this.seq = seq;
        this.classSeq = classSeq;
        this.usePlace = usePlace;
        this.cardComp = cardComp;
        this.cardNum = cardNum;
        this.useDate = useDate;
        this.money = money;
        this.usehistSubmitInfo = usehistSubmitInfo;
    }
}
