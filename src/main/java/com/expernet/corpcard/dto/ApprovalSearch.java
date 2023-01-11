package com.expernet.corpcard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApprovalSearch {
    String stateCd;
    List<String> deptList;
    List<String> teamList;
    String writerNm;
    String startDate;
    String endDate;

    @Builder
    public ApprovalSearch(String stateCd, List<String> deptList, List<String> teamList, String writerNm,
                          String startDate, String endDate){
        this.stateCd = stateCd;
        this.deptList = deptList;
        this.teamList = teamList;
        this.writerNm = writerNm;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
