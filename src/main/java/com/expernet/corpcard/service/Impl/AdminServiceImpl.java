package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.common.ApprovalDTO;
import com.expernet.corpcard.dto.admin.ApprovalListDTO;
import com.expernet.corpcard.dto.admin.UserListDTO;
import com.expernet.corpcard.dto.admin.AuthDTO;
import com.expernet.corpcard.dto.admin.CardInfoDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Transactional(rollbackFor = SQLException.class)
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
     * @param adminYn : 관리자 여부
     */
    @Override
    public List<UserListDTO.Response> getManagerList(String adminYn) {
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn(adminYn);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userEntityList, new TypeToken<List<UserListDTO.Response>>() {
        }.getType());
    }

    /**
     * 관리자 권한 변경
     *
     * @param params : 권한 변경할 사용자 정보
     */
    @Override
    public Object updateAuth(AuthDTO.PatchReq params) {
        List<User> userInfoList = userRepository.findAllByUserIdIn(params.getUserIdList());
        List<UserAddInfo> addInfoList = new ArrayList<>();

        for (User user : userInfoList) {
            UserAddInfo info = user.getUserAddInfo();
            info.setAdminYn(params.getAdminYn());
            addInfoList.add(info);
        }

        return userAddInfoRepository.saveAllAndFlush(addInfoList);
    }

    /**
     * 카드 목록 조회
     */
    @Override
    public List<CardInfo> getCardList() {
        return cardInfoRepository.findAll();
    }

    /**
     * 카드 정보 삭제
     *
     * @param cardSeqList : 카드 시퀀스 목록
     */
    @Override
    public long deleteCardList(List<Long> cardSeqList) throws SQLException {
        long result;

        int prevCnt = cardInfoRepository.findAll().size();
        cardReceiptentRepository.deleteAllByCardInfo_SeqIn(cardSeqList);
        cardInfoRepository.deleteAllByIdInBatch(cardSeqList);
        int afterCnt = cardInfoRepository.findAll().size();
        if (prevCnt - afterCnt == cardSeqList.size()) {
            result = cardSeqList.size();
        }else{
            throw new SQLException();
        }
        
        return result;
    }

    /**
     * 카드 정보 저장 or 수정
     *
     * @param params : 카드 정보
     */
    @Override
    public CardInfo saveCardInfo(CardInfoDTO.PostReq params) {
        CardInfo result = null;

        //1.카드 정보 저장 or 수정
        CardInfo cardInfo = CardInfo.builder()
                .seq(params.getCardSeq())
                .cardComp(params.getCardComp())
                .cardNum(params.getCardNum())
                .build();
        result = cardInfoRepository.saveAndFlush(cardInfo);
        List<CardReceiptent> receiptents = cardReceiptentRepository.findAllByCardInfo_Seq(params.getCardSeq());

        //2.카드 수령인 저장 or 수정(반납)
        if (!receiptents.isEmpty()) {
            CardReceiptent receiptent = receiptents.get(receiptents.size() - 1);
            if (receiptent.getReturnedAt() != null) {
                if (params.getUserId() != null) {
                    saveReceiptentByParams(params, result);
                }
            } else {
                if (params.getUserId() == null) {
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                } else if (!params.getUserId().equals(receiptent.getUser().getUserId())) {
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                    saveReceiptentByParams(params, result);
                }
            }
        } else {
            if (params.getUserId() != null) {
                saveReceiptentByParams(params, result);
            }
        }

        return result;
    }

    /**
     * 사용자 전체 목록 조회
     */
    @Override
    public List<UserListDTO.Response> getUserList() {
        List<User> userList = userRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userList, new TypeToken<List<UserListDTO.Response>>() {}.getType());
    }

    /**
     * 결제 내역 조회
     *
     * @param wrtYm: 작성연월
     */
    @Override
    public HashMap<String, Object> getPayList(String wrtYm) {
        HashMap<String, Object> result = null;
        List<UsehistSubmitInfo> submitInfos = usehistSubmitInfoRepository.findByWrtYm(wrtYm);
        List<Long> seqList = new ArrayList<>();

        for (UsehistSubmitInfo submitInfo : submitInfos) {
            seqList.add(submitInfo.getSeq());
        }

        List<CardUsehist> list = cardUsehistRepository.findAllByUserhistSubmitInfo_SeqIn(seqList);
        if (list.size() > 0) {
            result = new HashMap<>();
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
    public Dept getUpperDeptInfo() {
        List<Dept> deptList = deptRepository.findAllByUpper_deptCd(null);
        return deptList.get(0);
    }

    /**
     * 결재 건 목록 조회
     *
     * @param params : 검색 조건
     */
    @Override
    public List<HashMap<String, Object>> getApprovalList(ApprovalListDTO.Request params) {
        List<String> teamList = new ArrayList<>();
        String teamCd = params.getTeam();
        String deptCd = params.getDept();
        String[] submitDate = params.getSubmitDate().split(" - ");

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
        ApprovalDTO approvalSearch = ApprovalDTO.builder()
                .stateCd("C")
                .teamList((teamList.size() == 0) ? null : teamList)
                .writerNm((params.getWriterNm() != null) ? params.getWriterNm() : null)
                .startDate(submitDate[0])
                .endDate(submitDate[1])
                .build();

        //3.결재 건 목록 조회
        return usehistSubmitInfoRepository.findByParams(approvalSearch);
    }

    /**
     * 카드 수령인 정보 저장
     *
     * @param params  : 카드 정보 DTO
     * @param cardInfo : 카드 정보
     */
    private void saveReceiptentByParams(CardInfoDTO.PostReq params, CardInfo cardInfo) {
        User userInfo = userRepository.findByUserId(params.getUserId());
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
