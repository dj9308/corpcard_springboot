package com.expernet.corpcard.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CardInfoDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostReq{
        @NotBlank
        private long cardSeq;
        @NotBlank
        private String cardComp;
        @NotBlank
        @Pattern(regexp = "^\\d{4}\\-\\d{4}\\-\\d{4}\\-\\d{4}$",
                message = "카드번호 형식이 'xxxx-xxxx-xxxx-xxxx'여야 합니다.")
        private String cardNum;
        private long userSeq;
        private String userId;
        private String userNm;
    }
}
