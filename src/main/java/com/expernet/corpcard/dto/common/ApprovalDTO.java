package com.expernet.corpcard.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApprovalDTO {
    String stateCd;
    List<String> deptList;
    List<String> teamList;
    String writerNm;
    String startDate;
    String endDate;

    @Builder
    public ApprovalDTO(String stateCd, List<String> deptList, List<String> teamList, String writerNm,
                       String startDate, String endDate){
        this.stateCd = stateCd;
        this.deptList = deptList;
        this.teamList = teamList;
        this.writerNm = writerNm;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
