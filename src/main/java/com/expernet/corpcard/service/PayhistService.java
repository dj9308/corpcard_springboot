package com.expernet.corpcard.service;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.UsehistSubmitInfo;

import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제내역 Service Class
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
public interface PayhistService {

    /**
     * 법인카드 사용내역 조회
     * @param paramMap: 제출 seq
     */
    List<CardUsehist> searchCardUsehistList(HashMap<String, Object> paramMap);

    /**
     * 법인카드 사용내역 저장
     * @param paramMap: 사용내역 정보
     */
    long saveCardUsehistList(HashMap<String, Object> paramMap);

    /**
     * 법인카드 사용내역 삭제
     * @param cardUsehistList: 사용내역 list
     */
    long deleteCardUsehistList(List<CardUsehist> cardUsehistList);

    /**
     * 법인카드 사용내역 수정
     * @param cardUsehistList: 사용내역 list
     */
    long updateCardUsehistList(List<CardUsehist> cardUsehistList);
}
