package com.expernet.corpcard.controller;

import com.expernet.corpcard.dto.payhist.PayhistDTO;
import com.expernet.corpcard.dto.payhist.SearchPayhistListDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.service.PayhistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제 내역 Controller Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.11.18	설동재	최초 생성
 *
 * </pre>
 * @since 2022.11.18
 */
@Controller
@RequestMapping(value = "/payhist")
public class PayhistController {
    /**
     * 결제 내역 Service
     */
    @Resource(name = "PayhistService")
    private PayhistService payhistService;

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PayhistController.class);

    /**
     * 결제내역 페이지
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String payhistView(Model model) {
        model.addAttribute("menu", "payhist");
        return "payhist";
    }

    /**
     * 결제 내역 목록 조회
     *
     * @param params : 검색 조건
     * @param model    : modelMap
     */
    @RequestMapping(value = "/searchList", method = RequestMethod.GET)
    public String searchPayhistList(@Valid SearchPayhistListDTO.request params, ModelMap model) {
        HashMap<String, Object> result = null;
        try {
            result = payhistService.searchCardUsehistList(params);
        } finally {
            if (result != null && result.get("list") != null) {
                model.addAttribute("result", result);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", params.getWrtYm() + "의 결제내역 조회 성공");
                logger.info(params.getWrtYm() + "의 결제내역 조회 성공.");
            } else {
                model.addAttribute("CODE", "EMPTY");
                model.addAttribute("MSG", "결제내역 없음");
                logger.info("결제내역 없음");
            }
        }
        return "jsonView";
    }

    /**
     * 결제 내역 저장
     *
     * @param cardUsehist : 결제 내역 정보
     * @param model       : modelMap
     */
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST)
    public String savePayhistList(@RequestBody CardUsehist cardUsehist, ModelMap model) {
        Object result = null;
        try {
            result = payhistService.saveCardUsehistInfo(cardUsehist);

        } finally {
            if (result != null) {
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "결제 내역 저장 성공");
                logger.info("결제 내역 저장 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "결제 내역 저장 실패");
                logger.error("결제 내역 저장 실패");
            }
        }

        return "jsonView";
    }

    /**
     * 결제 내역 수정
     *
     * @param cardUsehist : 사용 내역
     * @param model       : modelMap
     */
    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    public String updatePayhistInfo(@RequestBody CardUsehist cardUsehist, ModelMap model) {
        Object result = null;
        try {
            result = payhistService.updateCardUsehistInfo(cardUsehist);
        } finally {
            if (result != null) {
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "결제 내역 수정 성공");
                logger.info("결제 내역 수정 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "결제 내역 수정 실패");
                logger.error("결제 내역 수정 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 결제 내역 리스트 삭제
     *
     * @param paramMap : row seq list
     * @param model    : modelMap
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.DELETE)
    public String deletePayhistList(@RequestParam HashMap<String, Object> paramMap, ModelMap model) {
        long result = 0;
        try {
            result = payhistService.deleteCardUsehistInfo(paramMap);
        } catch (JsonProcessingException e) {
            model.addAttribute("CODE", "ERR");
            model.addAttribute("MSG", "결제 내역 삭제 실패");
            logger.error("결제 내역 삭제 실패");
        } finally {
            if (result > 0) {
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "결제 내역 " + result + "건 삭제 성공");
                logger.info("결제 내역 " + result + "건 삭제 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "결제 내역 삭제 실패");
                logger.error("결제 내역 삭제 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 법인카드 결제 내역 제출
     *
     * @param paramMap : 제출 정보
     * @param model    : modelMap
     */
    @RequestMapping(value = "/updateState", method = RequestMethod.PATCH)
    public String updateStateSeq(@RequestParam HashMap<String, Object> paramMap, ModelMap model) {
        Object result = null;
        try {
            result = payhistService.updateStateSeq(paramMap);
        } finally {
            if (result != null) {
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "결제 내역 제출 성공");
                logger.info("제출 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "결제 내역 제출 실패");
                logger.error("결제 내역 제출 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 첨부파일 조회
     *
     * @param paramMap  : 제출 정보
     * @param model     : modelMap
     */
    @RequestMapping(value = "/searchAtchList", method = RequestMethod.GET)
    public String searchAtchList(@RequestParam HashMap<String, Object> paramMap, ModelMap model) {
        List<AttachmentInfo> result = null;
        try {
            result = payhistService.searchAtchList(paramMap);
        } finally {
            if (result != null) {
                if(result.isEmpty()){
                    model.addAttribute("CODE", "EMPTY");
                    model.addAttribute("MSG", "업로드된 첨부파일 없음");
                    logger.info("업로드된 첨부파일 없음");
                }else{
                    model.addAttribute("result", result);
                    model.addAttribute("CODE", "SUCCESS");
                    model.addAttribute("MSG", "첨부파일 조회 성공");
                    logger.info("첨부파일 조회 성공");
                }
            }else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "첨부파일 조회 실패");
                logger.error("첨부파일 조회 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 첨부파일 업로드
     *
     * @param paramMap  : 제출 정보
     * @param fileList  : 업로드된 파일 list
     * @param model     : modelMap
     */
    @RequestMapping(value = "/uploadAtch", method = RequestMethod.POST, produces = "application/text; charset=utf8")
    public String uploadAtch(@RequestPart(value = "key") HashMap<String, Object> paramMap,
                             @RequestPart(value = "files") List<MultipartFile> fileList,
                             ModelMap model) {
        List<AttachmentInfo> result = new ArrayList<>();
        try {
            result = payhistService.uploadAtch(paramMap, fileList);
        } finally {
            if (fileList.size() == result.size()) {
                model.addAttribute("result", result);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", result.size()+"건의 첨부파일 업로드 성공");
                logger.info(result.size()+"건의 첨부파일 업로드 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "첨부파일 업로드 실패");
                logger.error("첨부파일 업로드 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 업로드된 첨부파일 리스트 삭제
     *
     * @param paramMap  : 삭제할 첨부파일 Seq List
     * @param model     : modelMap
     */
    @RequestMapping(value = "/deleteAtchList", method = RequestMethod.DELETE)
    public String deleteAtchList(@RequestParam HashMap<String, Object> paramMap,
                             ModelMap model) {
        long result = 0;
        try {
            result = payhistService.deleteAtch(paramMap);
        } finally {
            if (result > 0) {
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "업로드된 파일 삭제 성공");
                logger.info("업로드된 파일 삭제 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "업로드된 파일 삭제 실패");
                logger.error("업로드된 파일 삭제 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 첨부파일 다운로드
     *
     * @param paramMap  : 첨부파일 Info List
     * @param response  : HttpServletResponse
     */
    @RequestMapping(value = "/downloadAtch", method = RequestMethod.GET)
    public void downloadAtch(@RequestParam HashMap<String, Object> paramMap, HttpServletResponse response) {
        try {
            payhistService.downloadAtch(paramMap, response);
            logger.info("업로드된 파일 삭제 성공");
        } catch (IOException e) {
            logger.error("업로드된 파일 삭제 실패");
            throw new RuntimeException(e);
        }
    }
}
