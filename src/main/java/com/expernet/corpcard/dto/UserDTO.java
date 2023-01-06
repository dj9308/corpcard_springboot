package com.expernet.corpcard.dto;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.UserAddInfo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserDTO {
    private long seq;
    private String userId;
    private UserAddInfo userAddInfo;
    private String ofcds;
    private String userNm;
    private Character chiefYn;
    private DeptDTO dept;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
