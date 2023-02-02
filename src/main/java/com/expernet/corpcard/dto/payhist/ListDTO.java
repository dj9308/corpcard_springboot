package com.expernet.corpcard.dto.payhist;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ListDTO {
    @Getter
    @Setter
    public static class Request {
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
        private String classCd;   //상태 Code
    }
    @Getter
    @Setter
    public static class DeleteReq {
        private String writerId;      //작성자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;         //작성연월
        private List<Long> seqList;   //삭제 ID 목록
    }
}
