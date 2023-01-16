package com.expernet.corpcard.dto;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private long seq;
        private String userId;
        private UserAddInfoDTO userAddinfo;
        private String ofcds;
        private String userNm;
        private Character chiefYn;
        private DeptDTO dept;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        @Builder
        public Response(User entity) {
            this.seq = entity.getSeq();
            this.userId = entity.getUserId();
            this.ofcds = entity.getOfcds();
            this.userAddinfo = new UserAddInfoDTO(entity.getUserAddInfo());
            this.userNm = entity.getUserNm();
            this.chiefYn = entity.getChiefYn();
            this.dept = new DeptDTO(entity.getDept());
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserAddInfoDTO {
        private long seq;
        private String userPasswd;
        private String adminYn;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public UserAddInfoDTO(UserAddInfo entity) {
            this.seq = entity.getSeq();
            this.userPasswd = entity.getUserPasswd();
            this.adminYn = entity.getAdminYn();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeptDTO {
        private long seq;
        private String deptNm;
        private String deptCd;
        private String upperDeptCd;
        private String chiefTitle;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private List<DeptDTO> lower;

        public DeptDTO(Dept entity) {
            this.seq = entity.getSeq();
            this.deptNm = entity.getDeptNm();
            this.deptCd = entity.getDeptCd();
            this.upperDeptCd = entity.getUpperDeptCd();
            this.chiefTitle = entity.getChiefTitle();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
            this.lower = entity.getLower().stream().map(DeptDTO::new).collect(Collectors.toList());
        }
    }
}
