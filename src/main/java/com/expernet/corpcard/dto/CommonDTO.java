package com.expernet.corpcard.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

public class CommonDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class SearchCardList {
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
    }
}
