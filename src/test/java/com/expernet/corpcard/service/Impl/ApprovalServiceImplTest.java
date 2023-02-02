package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.common.ApprovalDTO;
import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class ApprovalServiceImplTest {
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeptRepository deptRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void searchApprovalList() {
        //given
        List<String> teamList = new ArrayList<>();
        String team = "ALL";
        String userId = "9";
        String upperDeptCd = "4";
        String[] submitDate = "2022-10 - 2023-01".split(" - ");

        //when
        //1.부서 list 생성
        User userInfo = userRepository.findByUserId(userId);
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
        ApprovalDTO approvalSearch = ApprovalDTO.builder()
                .teamList((teamList.size() == 0) ? null : teamList)
                .startDate(submitDate[0])
                .endDate(submitDate[1])
                .build();

        //then
        List<HashMap<String, Object>> list = usehistSubmitInfoRepository.findByParams(approvalSearch);
        assertNotNull(list);
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