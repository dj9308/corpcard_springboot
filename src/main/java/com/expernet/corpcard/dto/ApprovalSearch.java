package com.expernet.corpcard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApprovalSearch {
    List<String> deptList;
    List<String> teamList;
    String writerNm;
    String startDate;
    String endDate;

    @Builder
    public ApprovalSearch(List<String> deptList, List<String> teamList, String writerNm,
                          String startDate, String endDate){
        this.deptList = deptList;
        this.teamList = teamList;
        this.writerNm = writerNm;
        this.startDate = startDate;
        this.endDate = endDate;

    }
}
