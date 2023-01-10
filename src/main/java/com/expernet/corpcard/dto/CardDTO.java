package com.expernet.corpcard.dto;

import com.expernet.corpcard.entity.CardInfo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CardDTO {
    private long cardSeq;
    private long receiptentSeq;
    private String cardComp;
    private String cardNum;
    private String userId;
    private String userNm;
}
