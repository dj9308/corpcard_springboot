package com.expernet.corpcard.controller;

import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.service.AdminService;
import com.expernet.corpcard.service.ApprovalService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

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
	 * 관리자 Service
	 */
	@Resource(name = "AdminService")
	private AdminService adminService;

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

	/**
	 * 관리자 목록 조회
	 * @param model: modelMap
	 */
	@RequestMapping("/searchManagerList")
	public String searchApprovalList(Model model){
		List<UserDTO> result = null;
		try {
			result = adminService.searchManagerList();
		} finally {
			if (result != null) {
				if(result.size() == 0){
					model.addAttribute("CODE", "EMPTY");
					model.addAttribute("MSG", "관리자 목록 없음");
					logger.info("관리자 목록 없음");
				}else{
					model.addAttribute("result", result);
					model.addAttribute("CODE", "SUCCESS");
					model.addAttribute("MSG", "관리자 목록 조회 성공");
					logger.info("관리자 목록 조회 성공");
				}
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "관리자 목록 조회 실패");
				logger.error("관리자 목록 조회 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 관리자 권한 변경
	 * @param paramMap: modelMap
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/updateAuth", method = RequestMethod.PATCH)
	public String updateAuth(@RequestParam HashMap<String, Object> paramMap, Model model){
		Object result = null;
		try {
			result = adminService.updateAuth(paramMap);
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "관리자 권한 변경 성공");
				logger.info("관리자 권한 변경 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "관리자 권한 변경 실패");
				logger.error("관리자 권한 변경 실패");
			}
		}
		return "jsonView";
	}
}
