package com.expernet.corpcard.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CARD_INFO")
@NoArgsConstructor
public class CardInfo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @Column(name = "CARD_COMP")
    private String cardComp;

    @Column(name = "CARD_NUM")
    private String cardNum;

    @JsonManagedReference
    @OneToMany(mappedBy = "cardInfo", fetch = FetchType.LAZY)
    private List<CardReceiptent> cardReceiptents = new ArrayList<>();
    @Builder
    public CardInfo(long seq, String cardComp, String cardNum){
        this.seq = seq;
        this.cardComp = cardComp;
        this.cardNum = cardNum;
    }
}
