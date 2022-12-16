package com.expernet.corpcard.controller;

import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.ClassInfo;
import com.expernet.corpcard.service.CommonService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Base Controller Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.11.08	설동재	최초 생성
 *
 * </pre>
 * @since 2022.11.08
 */
@Controller
public class CommonController {
    /**
     * base Service
     */
    @Resource(name = "CommonService")
    private CommonService baseService;

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * 분류 목록 조회
     * @param model    : modelMap
     */
    @RequestMapping("/base/classList")
    public String searchClassList(Model model){
        List<ClassInfo> classList = new ArrayList<>();
        try{
            classList = baseService.searchClassList();
        }finally {
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
     * @param model    : modelMap
     */
    @RequestMapping("/base/cardList")
    public String searchCardList(Model model){
        List<CardInfo> cardList = new ArrayList<>();
        try{
            cardList = baseService.searchCardList();
        }finally {
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
}
