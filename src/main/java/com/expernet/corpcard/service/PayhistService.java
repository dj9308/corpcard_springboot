package com.expernet.corpcard.service;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

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
     * 법인카드 결제 내역 목록 조회
     * @param paramMap: 제출 seq
     */
    HashMap<String, Object> searchCardUsehistList(HashMap<String, Object> paramMap);

    /**
     * 법인카드 결제 내역 딘일 정보 조회
     * @param paramMap: 결제 내역 seq
     */
    CardUsehist searchCardUsehistInfo(HashMap<String, Object> paramMap);

    /**
     * 법인카드 결제 내역 저장
     * @param cardUsehist: 결제 내역 정보
     * @param userId : 사용자 ID
     */
    Object saveCardUsehistInfo(CardUsehist cardUsehist, String userId);

    /**
     * 법인카드 결제 내역 삭제
     * @param paramMap): 제출 정보 & 결제내역 seq list
     */
    long deleteCardUsehistInfo(HashMap<String, Object> paramMap) throws JsonProcessingException;

    /**
     * 법인카드 결제 내역 수정
     * @param cardUsehist: 결제 내역
     */
    Object updateCardUsehistInfo(CardUsehist cardUsehist);

    /**
     * 제출
     */
    Object updateStateSeq();
}
