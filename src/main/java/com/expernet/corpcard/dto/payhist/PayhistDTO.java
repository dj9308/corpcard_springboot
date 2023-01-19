package com.expernet.corpcard.dto.payhist;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class PayhistDTO {
    /**
     * 결제 내역 목록 조회 Request
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SearchListReq {
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
        private String classCd;   //상태 Code
    }

    /**
     * 월별 총계 조회 Request
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class searchTotalSumListReq {
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String startYm;
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String endYm;
        @Nullable
        private String userId;
    }

    /**
     * 월별 총계 조회 Response
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class searchTotalSumListRes {
        private Object seq;
        private Object wrtYm;
        private Object sum;
    }
}
