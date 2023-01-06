package com.expernet.corpcard.service;

import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.User;

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
     */
    List<UserDTO> searchManagerList();

    /**
     * 관리자 권한 변경
     * @param paramMap : 권한 변경할 사용자 정보
     */
    Object updateAuth(HashMap<String, Object> paramMap);

}
