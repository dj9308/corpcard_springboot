package com.expernet.corpcard.dto.admin;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CardInfoDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostReq{
        @Nullable
        private long cardSeq;
        private String cardComp;
        private String cardNum;
        private String userId;
        private String userNm;
    }
}
