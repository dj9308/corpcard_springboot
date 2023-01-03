package com.expernet.corpcard.controller;

import com.expernet.corpcard.entity.UsehistSubmitInfo;
import com.expernet.corpcard.service.ApprovalService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

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

	@Resource(name = "ApprovalService")
	private ApprovalService approvalService;

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
	 * 부서 및 팀 조회
	 * @param paramMap: 사용자 ID
	 * @param model: modelMap
	 */
	@RequestMapping("/searchDeptInfo")
	public String selectDeptInfo(@RequestParam HashMap<String, Object> paramMap, Model model){
		HashMap<String, Object> result = new HashMap<>();
		try {
			result = approvalService.searchDeptInfo(paramMap);
		} finally {
			if (result != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "부서 및 팀 조회 성공");
				logger.info("부서 및 팀 조회 성공");
			} else {
				model.addAttribute("CODE", "EMPTY");
				model.addAttribute("MSG", "부서 및 팀 조회 실패");
				logger.info("부서 및 팀 조회 실패");
			}
		}
		return "jsonView";
	}

	/**
	 * 결재 건 목록 조회
	 * @param paramMap: 검색 조건
	 * @param model: modelMap
	 */
	@RequestMapping("/searchList")
	public String searchApprovalList(@RequestParam HashMap<String, Object> paramMap, Model model){
		List<HashMap<String, Object>> result = null;
		try {
			result = approvalService.searchApprovalList(paramMap);
		} finally {
			if (result != null) {
				if(result.size() == 0){
					model.addAttribute("CODE", "EMPTY");
					model.addAttribute("MSG", "결재 건 목록 없음");
					logger.info("결재 건 목록 없음");
				}else{
					model.addAttribute("result", result);
					model.addAttribute("CODE", "SUCCESS");
					model.addAttribute("MSG", "결재 건 목록 조회 성공");
					logger.info("결재 건 목록 조회 성공");
				}
			} else {
				model.addAttribute("CODE", "ERR");
				model.addAttribute("MSG", "결재 건 목록 조회 실패");
				logger.error("결재 건 목록 조회 실패");
			}
		}
		return "jsonView";
	}
}
