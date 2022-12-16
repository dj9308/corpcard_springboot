package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CARD_RECEIPTENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardReceiptent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CARD_SEQ")
    private CardInfo cardInfo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "RECEIVED_AT")
    private Timestamp receivedAt;

    @Column(name = "RETURNED_AT")
    private Timestamp returnedAt;

    @Builder
    public CardReceiptent(long seq, CardInfo cardInfo, String userId){
        this.seq = seq;
        this.cardInfo = cardInfo;
        this.userId = userId;
    }
}