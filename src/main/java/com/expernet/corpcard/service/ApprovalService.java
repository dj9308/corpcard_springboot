package com.expernet.corpcard.service;

import com.expernet.corpcard.entity.AttachmentInfo;
import com.expernet.corpcard.entity.CardUsehist;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결재 Service Class
 * @author (주)엑스퍼넷 설동재
 * @since 2022.12.00
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.12.00	설동재	최초 생성
 *
 * </pre>
 */
public interface ApprovalService {

    /**
     * 결재 목록 조회
     * @param paramMap: 사용자 정보
     */
    HashMap<String, Object> searchApprovalList(HashMap<String, Object> paramMap);
}
