package com.expernet.corpcard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
//@RequestMapping(value = "/payhist", method = RequestMethod.GET)
public class PayhistController extends BaseController {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PayhistController.class);

    /**
     * 결제내역 페이지
     */
    @RequestMapping(value = "/payhist", method = RequestMethod.GET)
    public String payhistView(Model model) {
        model.addAttribute("menu", "payhist");
        return "payhist";
    }

    /**
     * 월별 통계 조회
     * @param dataList  : 기간(년월)
     * @param model     : modelMap
     */
    @RequestMapping(value = "/staticInfo", method = RequestMethod.GET)
    public String searchStaticInfo(@RequestParam List<HashMap<String, Object>> dataList, ModelMap model) {
        int result = 0;
        try{

        } finally{

        }

        return "jsonView";
    }

    /**
     * 결제 내역 조회
     * @param dataList  : 작성연월
     * @param model     : modelMap
     */
    @RequestMapping(value = "/payhistList", method = RequestMethod.GET)
    public String searchPayhistInfo(@RequestParam List<HashMap<String, Object>> dataList, ModelMap model) {
        int result = 0;
        try{

        } finally{

        }

        return "jsonView";
    }
}
