package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.dto.CommonDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public List<CardInfo> searchCardList(CommonDTO.SearchCardList commonDTO) throws ParseException {
        List<CardInfo> result;
        String userId = commonDTO.getUserId();
        String wrtYm = commonDTO.getWrtYm();
        if(userId != null){
            //해당 월의 첫날 & 마지막 날 설정
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            Date parsedDate = dateFormat.parse(wrtYm);
            calendar.setTime(parsedDate);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            Timestamp startDate = new Timestamp(calendar.getTimeInMillis());

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Timestamp endDate = new Timestamp(calendar.getTimeInMillis());

            result = cardInfoRepository.findAllByUserIdAndReceivedAt(userId, startDate, endDate);
        }else{
            result = cardInfoRepository.findAll();
        }
        return result;
    }

    /**
     * 제출 정보 상태 수정
     *
     * @param stateDTO : 제출 정보 및 수정할 상태 정보
     */
    @Override
    public Object updateState(CommonDTO.UpdateState stateDTO) {
        Object result = null;
        UsehistSubmitInfo submitInfo = null;
        StateInfo stateInfo = null;

        //1.상태를 수정할 제출 정보 조회
        if (stateDTO.getSubmitSeq() != null) {
            submitInfo = usehistSubmitInfoRepository.findBySeq(stateDTO.getSubmitSeq());
        } else if (stateDTO.getWriterId() != null && stateDTO.getWrtYm() != null) {
            submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(
                    stateDTO.getWriterId(), stateDTO.getWrtYm());
        }
        //2.상태 변경
        if (submitInfo != null) {
            User checkerInfo;
            //확인자 정보 삽입
            if (stateDTO.getCheckerId() != null) {
                checkerInfo = userRepository.findByUserId(stateDTO.getCheckerId());
                submitInfo.setCheckerId(checkerInfo.getUserId());
                submitInfo.setCheckerNm(checkerInfo.getUserNm());
                submitInfo.setCheckerOfcds(checkerInfo.getOfcds());
            }
            //결재 완료일 삽입
            if (stateDTO.getStateCd().equals("C")) {
                submitInfo.setApproveDate(new Timestamp(System.currentTimeMillis()));
            }
            //상태 seq 삽입
            stateInfo = stateInfoRepository.findByStateCd(stateDTO.getStateCd());
            submitInfo.setStateInfo(stateInfo);

            result = usehistSubmitInfoRepository.save(submitInfo);
        }
        return result;
    }
}
