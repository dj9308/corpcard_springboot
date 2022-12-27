package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Transactional
@Service("ApprovalService")
public class ApprovalServiceImpl implements ApprovalService {
    /**
     * 사용내역 제출정보 Repository
     */
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 결재 목록 조회
     * @param paramMap: 사용자 정보
     */
    @Override
    public HashMap<String, Object> searchApprovalList(HashMap<String, Object> paramMap) {
        return null;
    }
}
