package com.expernet.corpcard.controller;

import com.expernet.corpcard.dto.admin.*;
import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 관리자 Controller Class
 * @author (주)엑스퍼넷 설동재
 * @since 2022.11.18
 * @version 1.0
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.11.18	설동재	최초 생성
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
	@RequestMapping(value = "/{menuType}",method = RequestMethod.GET)
	public String histPage(@PathVariable("menuType") String menuType, Model model){
		if(!menuType.equals("hist") &&		//결제내역 관리
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
	 * @param adminYn: 관리자 여부
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/managerList", method = RequestMethod.GET)
	public String getManagerList(@RequestParam(value = "adminYn") String adminYn, Model model){
		List<UserListDTO.Response> result = null;
		try {
			result = adminService.getManagerList(adminYn);
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
	 * @param params: 사용자 정보
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.PATCH)
	public String updateAuth(@Valid AuthDTO.PatchReq params, Model model){
		Object result = null;
		try {
			result = adminService.updateAuth(params);
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

	/**
	 * 카드 목록 조회
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/cardList", method = RequestMethod.GET)
	public String getCardList(Model model){
		List<CardInfo> result = null;
		try {
			result = adminService.getCardList();
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
	 * 카드 정보 삭제
	 * @param params: 카드 seq
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/cardList", method = RequestMethod.DELETE)
	public String deleteCardList(@Valid CardListDTO.DeleteReq params, Model model) {
		List<Long> cardSeqList = params.getCardSeqList();
		long result = -1;
		try {
			result = adminService.deleteCardList(cardSeqList);
		} catch(SQLException e){
			model.addAttribute("CODE", "ERR");
			model.addAttribute("MSG", "카드 정보 삭제 실패");
			logger.error("카드 정보 삭제 실패");
		} finally{
			if (result != -1) {
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "카드 정보 삭제 성공");
				logger.info("카드 정보 삭제 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "카드 정보 삭제 실패");
				logger.error("카드 정보 삭제 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 카드 정보 저장 or 수정
	 * @param params: 카드 정보
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/cardInfo", method = RequestMethod.POST)
	public String saveCardInfo(@Valid CardInfoDTO.PostReq params, Model model){
		CardInfo result = null;
		try {
			result = adminService.saveCardInfo(params);
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "카드 정보 저장 또는 수정 성공");
				logger.info("카드 정보 저장 또는 수정 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "카드 정보 저장 또는 수정 실패");
				logger.error("카드 정보 저장 또는 수정 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 사용자 목록 조회
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/userList", method = RequestMethod.GET)
	public String getUserList(Model model){
		List<UserListDTO.Response> result = null;
		try {
			result = adminService.getUserList();
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "사용자 목록 조회 성공");
				logger.info("사용자 목록 조회 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "사용자 목록 조회 실패");
				logger.error("사용자 목록 조회 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 결제 내역 조회
	 * @param wrtYm: 작성연월
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/payList", method = RequestMethod.GET)
	public String getPayList(@RequestParam(value = "wrtYm") String wrtYm, Model model){
		HashMap<String, Object> result = null;
		try {
			result = adminService.getPayList(wrtYm);
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "결제 내역 조회 성공");
				logger.info("결제 내역 조회 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "결제 내역 조회 실패");
				logger.error("결제 내역 조회 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 상위 부서 조회
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/upperDeptInfo", method = RequestMethod.GET)
	public String getUpperDeptInfo(Model model){
		Dept result = null;
		try {
			result = adminService.getUpperDeptInfo();
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "부서 정보 조회 성공");
				logger.info("부서 정보 조회 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "부서 정보 조회 실패");
				logger.error("부서 정보 조회 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 결재 건 목록 조회
	 * @param params: 검색 조건
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/approvalList", method = RequestMethod.GET)
	public String getApprovalList(@Valid ApprovalListDTO.Request params, Model model){
		List<HashMap<String, Object>> result = null;
		try {
			result = adminService.getApprovalList(params);
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "검색 건 목록 조회 성공");
				logger.info("검색 건 목록 조회 성공");
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "검색 건 목록 조회 실패");
				logger.error("검색 건 목록 조회 실패");
			}
		}
		return "jsonView";
	}
}
