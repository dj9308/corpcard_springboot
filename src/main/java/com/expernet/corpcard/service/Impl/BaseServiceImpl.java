package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.ClassInfo;
import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.expernet.corpcard.repository.CardInfoRepository;
import com.expernet.corpcard.repository.CardUsehistRepository;
import com.expernet.corpcard.repository.ClassInfoRepository;
import com.expernet.corpcard.repository.UsehistSubmitInfoRepository;
import com.expernet.corpcard.service.BaseService;
import com.expernet.corpcard.service.PayhistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
@Service("BaseService")
public class BaseServiceImpl implements BaseService {
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
     * 분류 목록 조회
     */
    @Override
    public List<ClassInfo> searchClassList() {
        return classInfoRepository.findAll();
    }

    /**
     * 카드 목록 조회
     */
    @Override
    public List<CardInfo> searchCardList() {
        return cardInfoRepository.findAll();
    }
}
