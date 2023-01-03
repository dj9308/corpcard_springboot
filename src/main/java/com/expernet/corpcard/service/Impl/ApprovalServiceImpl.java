package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.ApprovalSearch;
import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.CardUsehistRepository;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Transactional
@Service("ApprovalService")
public class ApprovalServiceImpl implements ApprovalService {
    /**
     * 사용내역 제출정보 Repository
     */
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 사용자 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 부서 Repository
     */
    @Autowired
    private DeptRepository deptRepository;

    /**
     * 부서 Repository
     */
    @Autowired
    private CardUsehistRepository cardUsehistRepository;

    /**
     * 부서 정보 조회
     *
     * @param paramMap: 팀장 ID
     */
    @Override
    public HashMap<String, Object> searchDeptInfo(HashMap<String, Object> paramMap) {
        HashMap<String, Object> result = new HashMap<>();
        String userId = paramMap.get("USER_ID").toString();
        User userInfo = userRepository.findByUserId(userId);
        String upperDeptCd = userInfo.getDept().getUpperDeptCd();
        if (upperDeptCd != null) {
            result.put("upperDeptInfo", deptRepository.findByDeptCd(upperDeptCd));
        }
        result.put("deptInfo", userInfo.getDept());
        return result;
    }

    /**
     * 결재 건 목록 조회
     *
     * @param paramMap: 검색 조건
     */
    @Override
    public List<HashMap<String, Object>> searchApprovalList(HashMap<String, Object> paramMap) {
        //TODO 리펙토링 필요
        //TODO CEO 분기점 기능 구현 필요
        List<String> teamList = new ArrayList<>();
        List<String> deptList = new ArrayList<>();
        String userId = paramMap.get("userId").toString();
        String team = paramMap.get("team").toString();
        String upperDeptCd = paramMap.get("dept").toString();
        String[] submitDate = paramMap.get("submitDate").toString().split(" - ");

        //1.부서 list 생성
        User userInfo = userRepository.findByUserId(userId);
        if(userInfo.getDept().getUpper() != null){
            if (team.equals("ALL")) {
                if (upperDeptCd.equals("ALL")) {
                    upperDeptCd = userInfo.getDept().getDeptCd();
                    deptList = deptRepository.findDeptNmByUpperCd(upperDeptCd);
                }else{
                    teamList = deptRepository.findDeptNmByUpperCd(upperDeptCd);
                }
            } else {
                teamList.add(deptRepository.findByDeptCd(team).getDeptNm());
            }
        }else{
            //CEO

        }

        //2.entity 생성
        ApprovalSearch approvalSearch = ApprovalSearch.builder()
                .deptList((deptList.size() == 0) ? null : deptList)
                .teamList((teamList.size() == 0) ? null : teamList)
                .writerNm((paramMap.get("writerNm") != null) ? paramMap.get("writerNm").toString() : null)
                .startDate(submitDate[0])
                .endDate(submitDate[1])
                .build();

        //3.결재 건 목록 조회
        return usehistSubmitInfoRepository.findByParams(approvalSearch);
    }

    /**
     * 법인카드 사용 내역 목록 조회
     * @param paramMap: 검색 조건(seq)
     */
    @Override
    public HashMap<String, Object> searchPayhistList(HashMap<String, Object> paramMap) {
        HashMap<String, Object> result = new HashMap<>();
        long seq = Long.parseLong(paramMap.get("SEQ").toString());

        List<CardUsehist> list = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(seq);

        if (list.size() > 0) {
            //사용 내역 리스트
            result.put("list", list);
            //분류별 합계
            result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeq(seq));
            //총계
            result.put("sum", cardUsehistRepository.selectTotalSumBySubmitSeq(seq));
        }
        return result;
    }
}
