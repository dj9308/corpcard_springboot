package com.expernet.corpcard.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

public class ApprovalListDTO {
    @Getter
    @Setter
    public static class Request{
        private String userId;
        private String team;
        private String dept;
        private String submitDate;
        private String writerNm;
    }
}
