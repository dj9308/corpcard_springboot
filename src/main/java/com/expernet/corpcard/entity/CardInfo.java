package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CARD_INFO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "CARD_COMP")
    private String cardComp;

    @Column(name = "CARD_NUM")
    private String cardNum;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public CardInfo(long seq, String cardComp, String cardNum){
        this.seq = seq;
        this.cardComp = cardComp;
        this.cardNum = cardNum;
    }
}
