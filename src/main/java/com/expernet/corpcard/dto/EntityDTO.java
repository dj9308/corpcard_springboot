package com.expernet.corpcard.dto;

import com.expernet.corpcard.entity.UserAddInfo;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EntityDTO {
    /**
     * 사용자 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class User {
        private long seq;
        private String userId;
        private com.expernet.corpcard.entity.UserAddInfo userAddInfo;
        private String ofcds;
        private String userNm;
        private Character chiefYn;
        private Dept dept;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    /**
     * 부서 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Dept {
        private long seq;
        private String deptNm;
        private String deptCd;
        private String upperDeptCd;
        private String chiefTitle;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private Dept upper;
        private final List<Dept> lower = new ArrayList<>();
    }

    /**
     * 사용자 추가 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserAddInfo {
        private long seq;
        private User user;
        private String userPasswd;
        private String adminYn;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    /**
     * 결제 내역
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CardUsehist {
        private long seq;
        private UsehistSubmitInfo UsehistSubmitInfo;
        private ClassInfo ClassInfo;
        private String useHist;
        private String cardComp;
        private String cardNum;
        private Timestamp useDate;
        private long money;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    /**
     * 제출 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UsehistSubmitInfo {
        private long seq;
        private StateInfo stateInfo;
        private String writerId;
        private String writerDept;
        private String writerTeam;
        private String writerOfcds;
        private String writerNm;
        private String wrtYm;
        private String checkerId;
        private String checkerOfcds;
        private String checkerNm;
        private Timestamp approveDate;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    /**
     * 분류 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ClassInfo {
        private long seq;
        private String classCd;
        private String classNm;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    /**
     * 상태 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class StateInfo {
        private String stateCd;
        private Long submitSeq;
        private String writerId;
        private String checkerId;
        private String wrtYm;
    }

}
