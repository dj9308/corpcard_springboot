package com.expernet.corpcard.service;


import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.ClassInfo;

import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * base Service Class
 * @author (주)엑스퍼넷 설동재
 * @since 2022.12.00
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.12.00	설동재	최초 생성
 *
 * </pre>
 */
public interface CommonService {
    /**
     * 분류 목록 조회
     */
    List<ClassInfo> searchClassList();

    /**
     * 카드 목록 조회
     */
    List<CardInfo> searchCardList();
}
