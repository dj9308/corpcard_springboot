package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.AdminDTO;
import com.expernet.corpcard.dto.ApprovalSearch;
import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.AdminService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.assertNotNull;

@Transactional
@RequiredArgsConstructor
@Service("AdminService")
public class AdminServiceImpl implements AdminService {
    /**
     * 사용자 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 추가 정보 Repository
     */
    @Autowired
    private UserAddInfoRepository userAddInfoRepository;

    /**
     * 카드 정보 Repository
     */
    @Autowired
    private final CardInfoRepository cardInfoRepository;

    /**
     * 카드 수령인 Repository
     */
    @Autowired
    private final CardReceiptentRepository cardReceiptentRepository;

    /**
     * 결제 내역 Repository
     */
    @Autowired
    private final CardUsehistRepository cardUsehistRepository;

    /**
     * 제출 정보 Repository
     */
    private final UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 부서 Repository
     */
    private final DeptRepository deptRepository;

    /**
     * 관리자 권한 조회
     *
     * @param paramMap : 사용자 정보
     */
    @Override
    public List<UserDTO.Response> searchManagerList(HashMap<String, Object> paramMap) {
        //TODO JPA FETCH 전략 확인 및 조회 속도 개선 필요
        String adminYn = paramMap.get("adminYn").toString();
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn(adminYn);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userEntityList, new TypeToken<List<UserDTO.Response>>() {
        }.getType());
    }

    /**
     * 관리자 권한 변경
     *
     * @param paramMap : 권한 변경할 사용자 정보
     */
    @Override
    public Object updateAuth(HashMap<String, Object> paramMap) {
        String adminYn = paramMap.get("adminYn").toString();
        String listJSON = paramMap.get("userIdList").toString();
        List<String> userIdList;
        try {
            userIdList = new ObjectMapper().readValue(listJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }

        List<User> userInfoList = userRepository.findAllByUserIdIn(userIdList);
        List<UserAddInfo> addInfoList = new ArrayList<>();

        for (User user : userInfoList) {
            UserAddInfo info = user.getUserAddInfo();
            info.setAdminYn(adminYn);
            addInfoList.add(info);
        }

        return userAddInfoRepository.saveAllAndFlush(addInfoList);
    }

    /**
     * 카드 목록 조회
     */
    @Override
    public List<CardInfo> searchCardList() {
        return cardInfoRepository.findAll();
    }

    /**
     * 카드 정보 삭제
     *
     * @param paramMap : 카드 정보
     */
    @Override
    public long deleteCardInfo(HashMap<String, Object> paramMap) {
        //TODO cascade 이용한 자식 엔티티 삭제 처리 필요
        long result = -1;
        String listJSON = paramMap.get("cardSeqList").toString();
        List<Long> cardSeqList;
        try {
            cardSeqList = new ObjectMapper().readValue(listJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return -1;
        }
        int prevCnt = cardInfoRepository.findAll().size();
        cardReceiptentRepository.deleteAllByCardInfo_SeqIn(cardSeqList);
        cardInfoRepository.deleteAllByIdInBatch(cardSeqList);
        int afterCnt = cardInfoRepository.findAll().size();

        if (prevCnt - afterCnt == cardSeqList.size()) {
            result = cardSeqList.size();
        }
        return result;
    }

    /**
     * 카드 정보 저장 or 수정
     *
     * @param cardParams : 카드 정보
     */
    @Override
    public CardInfo saveCardInfo(AdminDTO.saveCardInfoReq cardParams) {
        //TODO 저장 후 cardinfo onetomany column 조회 안되는 이유 확인 필요
        CardInfo result = null;

        //1.카드 정보 저장 or 수정
        CardInfo cardInfo = CardInfo.builder()
                .seq(cardParams.getCardSeq())
                .cardComp(cardParams.getCardComp())
                .cardNum(cardParams.getCardNum())
                .build();

        result = cardInfoRepository.saveAndFlush(cardInfo);
        List<CardReceiptent> receiptents = cardReceiptentRepository.findAllByCardInfo_Seq(cardParams.getCardSeq());
        //2.카드 수령인 저장 or 수정(반납)
        if (!receiptents.isEmpty()) {
            CardReceiptent receiptent = receiptents.get(receiptents.size() - 1);
            if (receiptent.getReturnedAt() != null) {
                if (cardParams.getUserId() != null) {
                    saveReceiptentByParams(cardParams, result);
                }
            } else {
                if (cardParams.getUserId() == null) {
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                } else if (!cardParams.getUserId().equals(receiptent.getUser().getUserId())) {
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                    saveReceiptentByParams(cardParams, result);
                }
            }
        } else {
            if (cardParams.getUserId() != null) {
                saveReceiptentByParams(cardParams, result);
            }
        }

        return result;
    }

    /**
     * 사용자 전체 목록 조회
     */
    @Override
    public List<UserDTO.Response> searchUserList() {
        List<User> userList = userRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userList, new TypeToken<List<UserDTO.Response>>() {
        }.getType());
    }

    /**
     * 결제 내역 조회
     *
     * @param wrtYm: 작성연월
     */
    @Override
    public HashMap<String, Object> searchPayList(String wrtYm) {
        HashMap<String, Object> result = new HashMap<>();
        List<UsehistSubmitInfo> submitInfos = usehistSubmitInfoRepository.findByWrtYm(wrtYm);
        List<Long> seqList = new ArrayList<>();

        for (UsehistSubmitInfo submitInfo : submitInfos) {
            seqList.add(submitInfo.getSeq());
        }

        List<CardUsehist> list = cardUsehistRepository.findAllByUserhistSubmitInfo_SeqIn(seqList);
        if (list.size() > 0) {
            //사용 내역 리스트
            result.put("list", list);
            //분류별 합계
            result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeqIn(seqList));
            //팀별 합계
            result.put("sumByTeam", cardUsehistRepository.selectSumDeptBySubmitSeqIn(seqList));
        }
        return result;
    }

    /**
     * 최상위 부서 조회
     */
    @Override
    public Dept searchTopDeptInfo() {
        List<Dept> deptList = deptRepository.findAllByUpper_deptCd(null);
        return deptList.get(0);
    }

    /**
     * 결재 건 목록 조회
     *
     * @param paramMap : 검색 조건
     */
    @Override
    public List<HashMap<String, Object>> searchApprovalList(HashMap<String, Object> paramMap) {
        List<String> teamList = new ArrayList<>();
        String teamCd = paramMap.get("team").toString();
        String deptCd = paramMap.get("dept").toString();
        String[] submitDate = paramMap.get("submitDate").toString().split(" - ");

        if (!deptCd.equals("ALL")) {
            if (teamCd.equals("ALL")) { //팀 전체
                Dept deptInfo = deptRepository.findByDeptCd(deptCd);
                List<Dept> subDeptList = deptInfo.getLower();
                searchSubDeptNm(teamList, subDeptList);
            } else {
                Dept teamInfo = deptRepository.findByDeptCd(teamCd);
                teamList.add(teamInfo.getDeptNm());
            }
        }

        //2.entity 생성
        ApprovalSearch approvalSearch = ApprovalSearch.builder()
                .stateCd("C")
                .teamList((teamList.size() == 0) ? null : teamList)
                .writerNm((paramMap.get("writerNm") != null) ? paramMap.get("writerNm").toString() : null)
                .startDate(submitDate[0])
                .endDate(submitDate[1])
                .build();

        //3.결재 건 목록 조회
        return usehistSubmitInfoRepository.findByParams(approvalSearch);
    }

    /**
     * 카드 수령인 정보 저장
     *
     * @param cardParams  : 카드 DTO
     * @param cardInfo : 카드 정보
     */
    private void saveReceiptentByParams(AdminDTO.saveCardInfoReq cardParams, CardInfo cardInfo) {
        User userInfo = userRepository.findByUserId(cardParams.getUserId());
        CardReceiptent cardReceiptent = CardReceiptent.builder()
                .cardInfo(cardInfo)
                .user(userInfo)
                .build();
        cardReceiptentRepository.save(cardReceiptent);
    }

    /**
     * 하위 부서 코드 조회
     *
     * @param subDeptList : 하위 부서 정보
     */
    private void searchSubDeptNm(List<String> result, List<Dept> subDeptList) {
        if (subDeptList.size() != 0) {
            for (Dept dept : subDeptList) {
                result.add(dept.getDeptNm());
                if (dept.getLower().size() != 0) {
                    searchSubDeptNm(result, dept.getLower());
                }
            }
        }
    }
}
