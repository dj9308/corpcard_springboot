package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.CardUsehistRepository;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.PayhistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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
     * 사용자 정보 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 부서 정보 Repository
     */
    @Autowired
    private DeptRepository deptRepository;

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
    public HashMap<String, Object> searchCardUsehistList(HashMap<String, Object> paramMap) {
        HashMap<String, Object> result = new HashMap<>();
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        if(submitInfo != null) {
            List<CardUsehist> list = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq());
            if(list.size() > 0){
                //제출 상태
                result.put("stateInfo", submitInfo.getStateInfo());
                //사용내역 리스트
                result.put("list", list);
                //분류별 합계
                result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeq(submitInfo.getSeq()));
                //총계
                result.put("sum", cardUsehistRepository.selectTotalSumBySubmitSeq(submitInfo.getSeq()));
            }
        }
        return result;
    }

    /**
     * 법인카드 결제 내역 딘일 정보 조회
     * @param paramMap: 결제 내역 seq
     */
    @Override
    public CardUsehist searchCardUsehistInfo(HashMap<String, Object> paramMap) {
        return cardUsehistRepository.findById(Long.valueOf(String.valueOf(paramMap.get("seq"))))
                .orElse(null);
    }

    /**
     * 법인카드 사용내역 저장
     * @param cardUsehist : 결제 내역 정보
     * @param userId : 사용자 ID
     */
    @Override
    public Object saveCardUsehistInfo(CardUsehist cardUsehist, String userId) {
        //1.제출 정보 존재여부 확인
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String wrtYn = format.format(cardUsehist.getUseDate());
        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(userId, wrtYn);

        //2.제출 정보 없을 시 생성
        if(submitInfo == null){
            User userInfo = userRepository.findByUserId(userId);
            Dept deptInfo = deptRepository.findByDeptCd(userInfo.getDeptCd());
            StateInfo stateInfo = StateInfo.builder().seq(1).build();

            UsehistSubmitInfo histInfo = UsehistSubmitInfo.builder()
                    .stateInfo(stateInfo)
                    .writerId(userInfo.getUserId())
                    .writerDept(deptInfo.getDeptNm())
                    .writerOfcds(userInfo.getOfcds())
                    .writerNm(userInfo.getUserNm())
                    .wrtYm(wrtYn)
                    .build();
            submitInfo = usehistSubmitInfoRepository.save(histInfo);
        }
        
        //3.결제내역 저장
        cardUsehist.setUsehistSubmitInfo(submitInfo);
        return cardUsehistRepository.save(cardUsehist);
    }

    /**
     * 법인카드 사용내역 삭제
     * @param paramMap: 제출 정보 & 삭제할 seq list
     */
    @Override
    public long deleteCardUsehistInfo(HashMap<String, Object> paramMap) throws JsonProcessingException {
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        String seqJSON = paramMap.get("SEQ_LIST").toString();
        List<Long> list = new ObjectMapper().readValue(seqJSON, new TypeReference<>() {});
        long result = 0;
        long preCnt = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq()).size();
        cardUsehistRepository.deleteAllById(list);
        long curCnt = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq()).size();
        if(preCnt > curCnt) {
            result = preCnt - curCnt;
        }
        return result;
    }

    /**
     * 법인카드 사용내역 수정
     * @param cardUsehist: 사용내역
     */
    @Override
    public Object updateCardUsehistInfo(CardUsehist cardUsehist) {
        return cardUsehistRepository.save(cardUsehist);
    }

    /**
     * 법인카드 결제 내역 제출
     * @param paramMap : 제출 정보
     */
    @Override
    public Object updateStateSeq(HashMap<String, Object> paramMap) {
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        submitInfo.setStateInfo(StateInfo.builder().seq(2).build());
        return usehistSubmitInfoRepository.save(submitInfo);
    }

    /**
     * 제출 내역 조회
     * @param paramMap: 사용자 정보 및 작성년월
     */
    private UsehistSubmitInfo searchSubmitInfo(HashMap<String, Object> paramMap){
        String writerId = paramMap.get("WRITER_ID").toString();
        String wrtYm = paramMap.get("WRT_YM").toString();
        return usehistSubmitInfoRepository.findByWriterIdAndWrtYm(writerId, wrtYm);
    }
}
