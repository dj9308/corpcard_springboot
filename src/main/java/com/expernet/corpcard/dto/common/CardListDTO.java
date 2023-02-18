package com.expernet.corpcard.dto.common;

import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.CardReceiptent;
import com.expernet.corpcard.entity.User;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CardListDTO {
    private long seq;
    private String cardComp;
    private String cardNum;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<CardReceiptentDTO> cardReceiptents = new ArrayList<>();
    @Builder
    public CardListDTO(CardInfo entity){
        this.seq = entity.getSeq();
        this.cardComp = entity.getCardComp();
        this.cardNum = entity.getCardNum();
        this.cardReceiptents = entity.getCardReceiptents().stream().map(CardReceiptentDTO::new)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CardReceiptentDTO{
        private long seq;
        private User user;
        private Timestamp receivedAt;
        private Timestamp returnedAt;
        @Builder
        public CardReceiptentDTO(CardReceiptent entity){
            this.seq = entity.getSeq();
            this.user = entity.getUser();
            this.receivedAt = entity.getReceivedAt();
            this.returnedAt = entity.getReturnedAt();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
    }
}
