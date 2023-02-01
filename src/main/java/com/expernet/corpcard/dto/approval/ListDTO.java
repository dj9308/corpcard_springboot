package com.expernet.corpcard.dto.approval;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ListDTO {

    @Getter
    @Setter
    public static class Request{
        @NotEmpty
        private String userId;
        private String team;
        private String dept;
        private String submitDate;
        private String writerNm;
    }
}
