package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.dto.CommonDTO;
import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.dto.common.SearchCardListDTO;
import com.expernet.corpcard.dto.common.SearchPayhistInfoDTO;
import com.expernet.corpcard.dto.common.SearchTotalSumListDTO;
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
    public User searchUserInfo(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 분류 목록 조회
     */
    @Override
    public List<ClassInfo> searchClassList() {
        return classInfoRepository.findAll();
    }

    /**
     * 카드 목록 조회
     *
     * @param commonDTO : 사용자 정보
     */
    @Override
    public List<SearchCardListDTO> searchCardList(SearchCardListDTO.Request commonDTO) {
        List<CardInfo> cardInfoList;
        String userId = commonDTO.getUserId();
        String wrtYm = commonDTO.getWrtYm();
        if(userId != null){
            cardInfoList = cardInfoRepository.findAllByUserIdAndReceivedAt(userId, wrtYm);
        }else{
            cardInfoList = cardInfoRepository.findAll();
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(cardInfoList, new TypeToken<List<SearchCardListDTO>>() {}.getType());
    }

    /**
     * 월별 총계 조회
     *
     * @param payhistDTO : 검색 조건
     */
    @Override
    public List<SearchTotalSumListDTO> searchTotalSumList(SearchTotalSumListDTO.Request payhistDTO) {
        return cardUsehistRepository.selectSumGroupByUserId(payhistDTO.getUserId(), payhistDTO.getStartYm(),
                payhistDTO.getEndYm());
    }

    /**
     * 법인카드 결제 내역 딘일 정보 조회
     * @param seq: 결제 내역 seq
     */
    @Override
    public SearchPayhistInfoDTO searchCardUsehistInfo(long seq) {
        SearchPayhistInfoDTO result = null;
        CardUsehist usehist = cardUsehistRepository.findById(seq).orElse(null);
        if(usehist != null){
            result = SearchPayhistInfoDTO.builder()
                    .seq(usehist.getSeq())
                    .classInfo(SearchPayhistInfoDTO.ClassInfo.builder().seq(usehist.getClassInfo().getSeq()).build())
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
     * @param stateParams : 제출 정보 및 수정할 상태 정보
     */
    @Override
    public Object updateState(CommonDTO.UpdateState stateParams) {
        Object result = null;
        UsehistSubmitInfo submitInfo = null;

        //1.상태를 수정할 제출 정보 조회
        if (stateParams.getSubmitSeq() != null) {
            submitInfo = usehistSubmitInfoRepository.findBySeq(stateParams.getSubmitSeq());
        } else if (stateParams.getWriterId() != null && stateParams.getWrtYm() != null) {
            submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(
                    stateParams.getWriterId(), stateParams.getWrtYm());
        }
        //2.상태 변경
        if (submitInfo != null) {
            User checkerInfo;
            //확인자 정보 삽입
            if (stateParams.getCheckerId() != null) {
                checkerInfo = userRepository.findByUserId(stateParams.getCheckerId());
                submitInfo.setCheckerId(checkerInfo.getUserId());
                submitInfo.setCheckerNm(checkerInfo.getUserNm());
                submitInfo.setCheckerOfcds(checkerInfo.getOfcds());
            }
            //결재 완료일 삽입
            if (stateParams.getStateCd().equals("C")) {
                submitInfo.setApproveDate(new Timestamp(System.currentTimeMillis()));
                submitInfo.setRejectMsg("");
            }
            //반려 사유 삽입
            if(stateParams.getStateCd().equals("D") && stateParams.getRejectMsg() != null){
                submitInfo.setRejectMsg(stateParams.getRejectMsg());
            }
            //상태 seq 삽입
            StateInfo stateInfo = stateInfoRepository.findByStateCd(stateParams.getStateCd());
            submitInfo.setStateInfo(stateInfo);

            result = usehistSubmitInfoRepository.save(submitInfo);
        }
        return result;
    }
}
