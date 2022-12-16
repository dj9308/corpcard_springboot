package com.expernet.corpcard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class ApprovalController {
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);

	@RequestMapping("/approval")
	public String page(Model model){
		model.addAttribute("menu", "approval");
		return "approval";
	}
}
