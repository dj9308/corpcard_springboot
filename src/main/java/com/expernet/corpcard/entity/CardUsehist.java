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

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "CLASS_SEQ")
//    private ClassInfo classInfo;

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

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public CardUsehist(long seq, UsehistSubmitInfo usehistSubmitInfo, long classSeq, String usePlace,
                       String cardComp, String cardNum, Timestamp useDate, long money){
        this.seq = seq;
        this.usehistSubmitInfo = usehistSubmitInfo;
        this.classSeq = classSeq;
//        this.classInfo = classInfo;
        this.usePlace = usePlace;
        this.cardComp = cardComp;
        this.cardNum = cardNum;
        this.useDate = useDate;
        this.money = money;
    }
}
