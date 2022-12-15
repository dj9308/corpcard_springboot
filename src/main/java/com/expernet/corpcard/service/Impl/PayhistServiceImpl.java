package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.expernet.corpcard.repository.CardUsehistRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.service.PayhistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제내역 Service Implement Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.12.00	설동재	최초 생성
 *
 * </pre>
 * @since 2022.12.00
 */
@Transactional
@Service("PayhistService")
public class PayhistServiceImpl implements PayhistService {
    /**
     * 사용내역 제출정보 Repository
     */
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 법인카드 사용내역 Repository
     */
    @Autowired
    private CardUsehistRepository cardUsehistRepository;

    /**
     * 법인카드 사용내역 조회
     * @param paramMap: 제출 seq
     */
    @Override
    public List<CardUsehist> searchCardUsehistList(HashMap<String, Object> paramMap) {
        List<CardUsehist> result = new ArrayList<>();
        String writerId = paramMap.get("WRITER_ID").toString();
        Date wrtYm = (Date) paramMap.get("WRT_YM");
        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(writerId, wrtYm);

        if(submitInfo != null) {
            result = cardUsehistRepository.findBySubmitSeq(submitInfo.getSeq());
        }

        return result;
    }

    /**
     * 법인카드 사용내역 저장
     * @param paramMap : 결제 내역 정보
     */
    @Override
    public long saveCardUsehistList(HashMap<String, Object> paramMap) {
        return 0;
    }

    /**
     * 법인카드 사용내역 삭제
     * @param cardUsehistList: 사용내역 list
     */
    @Override
    public long deleteCardUsehistList(List<CardUsehist> cardUsehistList) {
        return 0;
    }

    /**
     * 법인카드 사용내역 수정
     * @param cardUsehistList: 사용내역 list
     */
    @Override
    public long updateCardUsehistList(List<CardUsehist> cardUsehistList) {
        return 0;
    }
}
