package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.expernet.corpcard.entity.User;
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
    public List<UsehistSubmitInfo> searchApprovalList(HashMap<String, Object> paramMap) {
        List<String> deptList = new ArrayList<>();
        String userId = paramMap.get("userId").toString();
        String team = paramMap.get("team").toString();
        String upperDeptCd = paramMap.get("dept").toString();
        String[] submitDate = paramMap.get("submitDate").toString().split(" - ");
        String startDate = submitDate[0];
        String endDate = submitDate[1];
        String writerNm = (paramMap.get("writerNm") != null) ? paramMap.get("writerNm").toString() : null;

        //2.부서 list 생성
        if (team.equals("ALL")) {
            if (upperDeptCd.equals("ALL")) {
                upperDeptCd = userRepository.findByUserId(userId).getDept().getDeptCd();
            }
            deptList = deptRepository.findByUpperCd(upperDeptCd);
        } else {
            deptList.add(deptRepository.findByDeptCd(team).getDeptNm());
        }
        //3.결재 건 조회
        return usehistSubmitInfoRepository.findByParams(deptList, writerNm, startDate, endDate);
    }
}
