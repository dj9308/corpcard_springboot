package com.expernet.corpcard.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchTotalSumListDTO {
    private long seq;
    private String wrtYm;
    private long sum;

    /**
     * 결제 내역 목록 조회 Request
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class request {
        @NotBlank
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String startYm;
        @NotBlank
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String endYm;
        private String userId;
    }
}
