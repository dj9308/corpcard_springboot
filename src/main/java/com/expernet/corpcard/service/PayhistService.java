package com.expernet.corpcard.service;

import com.expernet.corpcard.dto.payhist.AtchListDTO;
import com.expernet.corpcard.dto.payhist.ListDTO;
import com.expernet.corpcard.entity.AttachmentInfo;
import com.expernet.corpcard.entity.CardUsehist;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제내역 Service Class
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
public interface PayhistService {

    /**
     * 법인카드 결제 내역 목록 조회
     * @param params: 제출 정보
     */
    HashMap<String, Object> getList(ListDTO.Request params);

    /**
     * 법인카드 결제 내역 저장
     * @param cardUsehist: 결제 내역 정보
     */
    Object saveInfo(CardUsehist cardUsehist);

    /**
     * 법인카드 결제 내역 삭제
     * @param seqList : 삭제할 결제 내역 목록
     */
    long deleteList(List<Long> seqList);

    /**
     * 첨부파일 조회
     * @param params  : 제출 정보
     */
    List<AttachmentInfo> getAtchList(AtchListDTO.Request params);

    /**
     * 첨부파일 업로드
     * @param params  : 제출 정보
     * @param fileList  : 업로드된 파일 list
     */
    List<AttachmentInfo> uploadAtch(HashMap<String, String> params, List<MultipartFile> fileList);

    /**
     * 첨부파일 삭제
     * @param seqList  : 첨부파일 seq list
     */
    long deleteAtch(List<Long> seqList);

    /**
     * 첨부파일 다운로드
     * @param paramMap  : 첨부파일 seq list
     * @param response  : HttpServletResponse
     */
    void downloadAtch(HashMap<String, Object> paramMap, HttpServletResponse response) throws IOException;
}
