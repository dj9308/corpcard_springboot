package com.expernet.corpcard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 관리자 Controller Class
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
@RequestMapping(value = "/admin")
public class AdminController {
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	/**
	 * 페이지 조회
	 * @param menuType: 관리자 소메뉴
	 * @param model: ModelMap
	 */
	@RequestMapping("/{menuType}")
	public String histPage(@PathVariable("menuType") String menuType, Model model){
		if(!menuType.equals("hist") &&	//결제내역 관리
				!menuType.equals("auth") && //권한 관리
				!menuType.equals("card")){	//카드 관리
			return "404";
		}else{
			model.addAttribute("menu", menuType);
			return "admin/"+menuType;
		}
	}
}
