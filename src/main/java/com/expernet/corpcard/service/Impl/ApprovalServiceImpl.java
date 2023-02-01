package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.ApprovalSearch;
import com.expernet.corpcard.dto.approval.ListDTO;
import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.CardUsehistRepository;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
     * @param userId: 사용자 ID
     */
    @Override
    public HashMap<String, Object> getDeptInfo(String userId) {
        HashMap<String, Object> result = new HashMap<>();
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
     * @param params: 검색 조건
     */
    @Override
    public List<HashMap<String, Object>> getList(ListDTO.Request params) {
        List<String> teamList = new ArrayList<>();
        String team = params.getTeam();
        String upperDeptCd = params.getDept();
        String[] submitDate = params.getSubmitDate().split(" - ");

        //1.부서 list 생성
        User userInfo = userRepository.findByUserId(params.getUserId());
        if (team.equals("ALL")) {
            if (upperDeptCd.equals("ALL")) {
                upperDeptCd = userInfo.getDept().getDeptCd();
                Dept deptInfo = deptRepository.findByDeptCd(upperDeptCd);
                searchSubDeptNm(teamList, deptInfo.getLower());
            }else{
                Dept deptInfo = deptRepository.findByDeptCd(upperDeptCd);
                searchSubDeptNm(teamList, deptInfo.getLower());
            }
        } else {
            teamList.add(deptRepository.findByDeptCd(team).getDeptNm());
        }
        //2.entity 생성
        ApprovalSearch approvalSearch = ApprovalSearch.builder()
                .teamList((teamList.size() == 0) ? null : teamList)
                .writerNm((params.getWriterNm() != null) ? params.getWriterNm() : null)
                .startDate(submitDate[0])
                .endDate(submitDate[1])
                .build();

        //3.결재 건 목록 조회
        return usehistSubmitInfoRepository.findByParams(approvalSearch);
    }

    /**
     * 법인카드 사용 내역 목록 조회
     * @param seq: 검색 조건
     */
    @Override
    public HashMap<String, Object> getPayhistList(long seq) {
        HashMap<String, Object> result = new HashMap<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "useDate");
        List<CardUsehist> list = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(seq, sort);
        if (list.size() > 0) {
            //사용 내역 리스트
            result.put("list", list);
            //분류별 합계
            result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeq(seq, null));
            //총계
            result.put("sum", cardUsehistRepository.selectTotalSumBySubmitSeq(seq, null));
        }
        return result;
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
