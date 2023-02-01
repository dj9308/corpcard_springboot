package com.expernet.corpcard.dto.payhist;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AtchListDTO {

    @Getter
    @Setter
    public static class Request{
        private long seq;
        private String writerId;
        @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
        private String wrtYm;
    }

    @Getter
    @Setter
    public static class DeleteReq{
        private List<Long> seqList;
    }
}
