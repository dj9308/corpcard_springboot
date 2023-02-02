package com.expernet.corpcard.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CardListDTO {
    @Getter
    @Setter
    public static class DeleteReq{
        private List<Long> cardSeqList;
    }
}
