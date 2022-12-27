package com.expernet.corpcard.controller;

import com.expernet.corpcard.entity.CardUsehist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제 내역 Controller Class
 * @author (주)엑스퍼넷 설동재
 * @since 2022.11.18
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.11.18	설동재	최초 생성
 *
 * </pre>
 */
@Controller
@RequestMapping(value = "/approval")
public class ApprovalController {
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);

	/**
	 * 결재 페이지
	 */
	@RequestMapping("")
	public String page(Model model){
		model.addAttribute("menu", "approval");
		return "approval";
	}

	/**
	 * 결재 리스트 조회
	 */
	@RequestMapping("/searchList")
	public String searchApprovalList(@RequestParam HashMap<String, Object> paramMap, Model model){
		HashMap<String, Object> result = null;
		try {

		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "결제내역 조회 성공");
				logger.info("결제내역 조회 성공");
			} else {
				model.addAttribute("CODE", "EMPTY");
				model.addAttribute("MSG", "결제내역 조회 실패");
				logger.info("결제내역 조회 실패");
			}
		}
		return "approval";
	}
}
