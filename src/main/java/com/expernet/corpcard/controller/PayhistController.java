package com.expernet.corpcard.controller;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.service.CommonService;
import com.expernet.corpcard.service.PayhistService;
import com.expernet.corpcard.service.SchedulerService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(value = "/payhist", method = RequestMethod.GET)
public class PayhistController {
    /**
     * payhist Service
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
     * 월별 통계 조회
     *
     * @param dataList : 기간(년월)
     * @param model    : modelMap
     */
    @RequestMapping(value = "/staticInfo", method = RequestMethod.GET)
    public String searchStaticInfo(@RequestParam List<HashMap<String, Object>> dataList, ModelMap model) {
        int result = 0;
        try {

        } finally {

        }

        return "jsonView";
    }

    /**
     * 결제 내역 조회
     *
     * @param paramMap : 작성연월
     * @param model    : modelMap
     */
    @RequestMapping(value = "/searchList", method = RequestMethod.GET)
    public String searchPayhistInfo(@RequestParam HashMap<String, Object> paramMap, ModelMap model) {
        List<CardUsehist> usehistList = new ArrayList<>();
        try {
            usehistList = payhistService.searchCardUsehistList(paramMap);
        } finally {
            if (usehistList != null && usehistList.size() > 0) {
                model.addAttribute("result", usehistList);
                model.addAttribute("CODE", "SUCCESS");
                model.addAttribute("MSG", paramMap.get("WRT_YM") + "의 결제내역 조회 성공");
                logger.info(paramMap.get("WRT_YM") + "의 결제내역 조회 성공.");
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
     * @param cardUsehist  : 결제 내역 정보
     * @param model : modelMap
     */
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST)
    public String savePayhistList(@RequestBody CardUsehist cardUsehist, ModelMap model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        Object result = null;

        try{
            result = payhistService.saveCardUsehistInfo(cardUsehist, userDetails.getUsername());
        }finally {
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
}
