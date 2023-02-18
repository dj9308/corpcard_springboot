package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CARD_USEHIST")
@NoArgsConstructor
public class CardUsehist extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBMIT_SEQ")
    private UsehistSubmitInfo usehistSubmitInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CLASS_SEQ")
    private ClassInfo classInfo;

    @Column(name = "USE_HIST")
    private String useHist;

    @Column(name = "CARD_COMP")
    private String cardComp;

    @Column(name = "CARD_NUM")
    private String cardNum;

    @Column(name = "USE_DATE")
    private Timestamp useDate;

    @Column(name = "MONEY")
    private long money;

    @Builder
    public CardUsehist(long seq, UsehistSubmitInfo usehistSubmitInfo, ClassInfo classInfo, String useHist,
                       String cardComp, String cardNum, Timestamp useDate, long money){
        this.seq = seq;
        this.usehistSubmitInfo = usehistSubmitInfo;
        this.classInfo = classInfo;
        this.useHist = useHist;
        this.cardComp = cardComp;
        this.cardNum = cardNum;
        this.useDate = useDate;
        this.money = money;
    }
}
