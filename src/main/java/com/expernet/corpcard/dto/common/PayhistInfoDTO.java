package com.expernet.corpcard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PayhistInfoDTO {
    private long seq;
    private ClassInfo classInfo;
    private String useHist;
    private String cardComp;
    private String cardNum;
    private Timestamp useDate;
    private long money;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ClassInfo{
        private long seq;
    }
}
