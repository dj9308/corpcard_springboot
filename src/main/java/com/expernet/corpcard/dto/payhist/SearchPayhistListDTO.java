package com.expernet.corpcard.dto.payhist;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@Builder
public class SearchPayhistListDTO {


    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class request{
        private String userId;  //사용자 ID
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;   //작성 연월
        private String classCd;   //상태 Code
    }
}
