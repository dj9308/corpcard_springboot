package com.expernet.corpcard.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AuthDTO {

    @Getter
    @Setter
    public static class PatchReq {
        private List<String> userIdList;
        private String adminYn;
    }
}
