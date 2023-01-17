package com.expernet.corpcard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class CommonDTO {

    /**
     * 카드 목록 조회
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SearchCardList {
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
    }

    /**
     * 제출 정보 상태 수정
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class UpdateState {
        @NotNull
        private final String stateCd;
        private final Long submitSeq;
        private final String writerId;
        private final String checkerId;
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private final String wrtYm;
        private String rejectMsg;
    }
}
