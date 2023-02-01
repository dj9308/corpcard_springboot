package com.expernet.corpcard.dto.common;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class StateInfoDTO {
    /**
     * 제출 정보 상태 수정 요청
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request {
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
