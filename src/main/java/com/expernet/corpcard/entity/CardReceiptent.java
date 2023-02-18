package com.expernet.corpcard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.bouncycastle.util.Times;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CARD_RECEIPTENT")
@NoArgsConstructor
public class CardReceiptent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_SEQ")
    private CardInfo cardInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @CreationTimestamp
    @Column(name = "RECEIVED_AT")
    private Timestamp receivedAt;

    @Column(name = "RETURNED_AT")
    private Timestamp returnedAt;

    @Builder
    public CardReceiptent(long seq, CardInfo cardInfo, User user, Timestamp receivedAt,
                          Timestamp returnedAt){
        this.seq = seq;
        this.cardInfo = cardInfo;
        this.user = user;
        this.receivedAt = receivedAt;
        this.returnedAt = returnedAt;
    }
}
