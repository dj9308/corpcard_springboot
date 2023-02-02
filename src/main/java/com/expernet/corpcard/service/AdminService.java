package com.expernet.corpcard.service;

import com.expernet.corpcard.dto.admin.ApprovalListDTO;
import com.expernet.corpcard.dto.admin.UserListDTO;
import com.expernet.corpcard.dto.admin.AuthDTO;
import com.expernet.corpcard.dto.admin.CardInfoDTO;
import com.expernet.corpcard.entity.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 관리자 Service Class
 * @author (주)엑스퍼넷 설동재
 * @since 2023.01.00
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2023.01.00	설동재	최초 생성
 *
 * </pre>
 */
public interface AdminService {
    /**
     * 관리자 목록 조회
     * @param adminYn : 권한 변경할 사용자 정보
     */
    List<UserListDTO.Response> getManagerList(String adminYn);

    /**
     * 관리자 권한 변경
     * @param params : 권한 변경할 사용자 정보
     */
    Object updateAuth(AuthDTO.PatchReq params);

    /**
     * 카드 목록 조회
     */
    List<CardInfo> getCardList();

    /**
     *
     * 카드 정보 삭제
     * @param cardSeqList : 카드 시퀀스 목록
     */
    long deleteCardList(List<Long> cardSeqList) throws SQLException;

    /**
     * 카드 정보 저장 or 수정
     * @param params : 카드 정보
     */
    CardInfo saveCardInfo(CardInfoDTO.PostReq params);

    /**
     * 사용자 전체 목록 조회
     */
    List<UserListDTO.Response> getUserList();

    /**
     * 결제 내역 조회
     * @param wrtYm: 작성연월
     */
    HashMap<String, Object> getPayList(String wrtYm);

    /**
     * 부서 조회
     */
    Dept getUpperDeptInfo();

    /**
     * 결재 건 목록 조회
     * @param params : 검색 조건
     */
    List<HashMap<String, Object>> getApprovalList(ApprovalListDTO.Request params);
}
