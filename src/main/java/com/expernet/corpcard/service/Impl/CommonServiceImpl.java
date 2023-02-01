package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.dto.common.CardListDTO;
import com.expernet.corpcard.dto.common.PayhistInfoDTO;
import com.expernet.corpcard.dto.common.StateInfoDTO;
import com.expernet.corpcard.dto.common.TotalSumListDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.CommonService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.*;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Base Service Implement Class
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
@Service("CommonService")
public class CommonServiceImpl implements CommonService {
    /**
     * 사용자 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 분류 정보 Repository
     */
    @Autowired
    private ClassInfoRepository classInfoRepository;

    /**
     * 카드 정보 Repository
     */
    @Autowired
    private CardInfoRepository cardInfoRepository;

    /**
     * 제출 정보 Repository
     */
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 상태 정보 Repository
     */
    @Autowired
    private StateInfoRepository stateInfoRepository;
    
    /**
     * 결제 내역 정보 Repository
     */
    @Autowired
    private CardUsehistRepository cardUsehistRepository;

    /**
     * 사용자 정보 조회
     *
     * @param userId : 로그인한 사용자 ID
     */
    @Override
    public User getUserInfo(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 분류 목록 전체 조회
     */
    @Override
    public List<ClassInfo> getClassList() {
        return classInfoRepository.findAll();
    }

    /**
     * 카드 목록 조회
     *
     * @param params : 사용자 정보
     */
    @Override
    public List<CardListDTO> getCardList(CardListDTO.Request params) {
        List<CardInfo> cardInfoList;

        if(params.getUserId() != null){
            cardInfoList = cardInfoRepository.findAllByUserIdAndReceivedAt(params.getUserId(),
                    params.getWrtYm());
        }else{
            cardInfoList = cardInfoRepository.findAll();
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(cardInfoList, new TypeToken<List<CardListDTO>>() {}.getType());
    }

    /**
     * 월별 총계 조회
     *
     * @param params : 검색 조건
     */
    @Override
    public List<TotalSumListDTO> getTotalSumList(TotalSumListDTO.Request params) {
        return cardUsehistRepository.selectSumGroupByUserId(params.getUserId(), params.getStartYm(),
                params.getEndYm());
    }

    /**
     * 법인카드 결제 내역 딘일 정보 조회
     * @param seq: 결제 내역 seq
     */
    @Override
    public PayhistInfoDTO getCardUsehistInfo(long seq) {
        PayhistInfoDTO result = null;
        CardUsehist usehist = cardUsehistRepository.findById(seq).orElse(null);
        if(usehist != null){
            result = PayhistInfoDTO.builder()
                    .seq(usehist.getSeq())
                    .classInfo(PayhistInfoDTO.ClassInfo.builder().seq(usehist.getClassInfo().getSeq()).build())
                    .useHist(usehist.getUseHist())
                    .cardComp(usehist.getCardComp())
                    .cardNum(usehist.getCardNum())
                    .useDate(usehist.getUseDate())
                    .money(usehist.getMoney())
                    .build();
        }
        return result;
    }

    /**
     * 제출 정보 상태 수정
     *
     * @param params : 제출 정보 및 수정할 상태 정보
     */
    @Override
    public Object patchState(StateInfoDTO.Request params) {
        Object result = null;
        UsehistSubmitInfo submitInfo = null;

        //1.상태를 수정할 제출 정보 조회
        if (params.getSubmitSeq() != null) {
            submitInfo = usehistSubmitInfoRepository.findBySeq(params.getSubmitSeq());
        } else if (params.getWriterId() != null && params.getWrtYm() != null) {
            submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(
                    params.getWriterId(), params.getWrtYm());
        }
        //2.상태 변경
        if (submitInfo != null) {
            User checkerInfo;
            //확인자 정보 삽입
            if (params.getCheckerId() != null) {
                checkerInfo = userRepository.findByUserId(params.getCheckerId());
                submitInfo.setCheckerId(checkerInfo.getUserId());
                submitInfo.setCheckerNm(checkerInfo.getUserNm());
                submitInfo.setCheckerOfcds(checkerInfo.getOfcds());
            }
            //결재 완료일 삽입
            if (params.getStateCd().equals("C")) {
                submitInfo.setApproveDate(new Timestamp(System.currentTimeMillis()));
                submitInfo.setRejectMsg("");
            }
            //반려 사유 삽입
            if(params.getStateCd().equals("D") && params.getRejectMsg() != null){
                submitInfo.setRejectMsg(params.getRejectMsg());
            }
            //상태 seq 삽입
            StateInfo stateInfo = stateInfoRepository.findByStateCd(params.getStateCd());
            submitInfo.setStateInfo(stateInfo);

            result = usehistSubmitInfoRepository.save(submitInfo);
        }
        return result;
    }
}
