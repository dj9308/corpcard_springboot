package com.expernet.corpcard.controller;

import com.expernet.corpcard.dto.CommonDTO;
import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.ClassInfo;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.service.CommonService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Common Controller Class
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
@Controller
@RequestMapping(value = "/common")
public class CommonController {
    /**
     * common Service
     */
    @Resource(name = "CommonService")
    private CommonService commonService;

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * 사용자 정보 조회
     *
     * @param model : modelMap
     */
    @RequestMapping("/searchUserInfo")
    public String searchUserInfo(Model model, Principal principal) {
        User userInfo = null;
        try {
            userInfo = commonService.searchUserInfo(principal.getName());
        } finally {
            if (userInfo != null) {
                model.addAttribute("result", userInfo);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "사용자 정보 조회 성공");
                logger.info("사용자 정보 조회 성공.");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "사용자 정보 조회 실패");
                logger.error("사용자 정보 조회 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 전체 분류 목록 조회
     *
     * @param model : modelMap
     */
    @RequestMapping("/classList")
    public String searchClassList(Model model) {
        List<ClassInfo> classList = new ArrayList<>();
        try {
            classList = commonService.searchClassList();
        } finally {
            if (classList != null) {
                model.addAttribute("result", classList);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "분류 목록 조회 성공");
                logger.info("분류 목록 조회 성공.");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "분류 목록 조회 실패");
                logger.error("분류 목록 조회 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 카드 목록 조회
     *
     * @param commonDTO : 검색 조건
     * @param model    : modelMap
     */
    @RequestMapping(value = "/cardList", method = RequestMethod.GET)
    public String searchCardList(@Valid CommonDTO.SearchCardList commonDTO, Model model) {
        List<CardInfo> cardList = new ArrayList<>();
        try {
            cardList = commonService.searchCardList(commonDTO);
        } catch (ParseException e) {
            model.addAttribute("CODE", "ERR");
            model.addAttribute("MSG", "카드 목록 조회 실패");
            logger.error("카드 목록 조회 실패");
        } finally {
            if (cardList != null) {
                model.addAttribute("result", cardList);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "카드 목록 조회 성공");
                logger.info("카드 목록 조회 성공.");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "카드 목록 조회 실패");
                logger.error("카드 목록 조회 실패");
            }
        }
        return "jsonView";
    }

    /**
     * 제출 정보 상태 수정
     *
     * @param commonDTO : 제출 정보 및 수정할 상태 정보
     * @param model    : modelMap
     */
    @RequestMapping(value = "/updateState", method = RequestMethod.PATCH)
    public String updateState(@Valid CommonDTO.UpdateState commonDTO, ModelMap model) {
        Object result = null;
        try {
            result = commonService.updateState(commonDTO);
        } finally {
            if (result != null) {
                model.addAttribute("result", result);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", "해당 제출 건의 상태 수정 성공");
                logger.info("해당 제출 건의 상태 수정 성공");
            } else {
                model.addAttribute("CODE", "ERR");
                model.addAttribute("MSG", "해당 제출 건의 상태 수정 실패");
                logger.error("해당 제출 건의 상태 수정 실패");
            }
        }
        return "jsonView";
    }
}
