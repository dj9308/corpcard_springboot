package com.expernet.corpcard.controller;

import com.expernet.corpcard.dto.approval.ListDTO;
import com.expernet.corpcard.service.ApprovalService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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
	/**
	 * 결재 Service
	 */
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
	 * @param principal: 사용자 정보
	 * @param model: modelMap
	 */
	@RequestMapping("/deptInfo")
	public String getDeptInfo(Principal principal, Model model){
		HashMap<String, Object> result = new HashMap<>();
		try {
			result = approvalService.getDeptInfo(principal.getName());
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
	 * @param params: 검색 조건
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String getList(@Valid ListDTO.Request params, Model model){
		List<HashMap<String, Object>> result = null;
		try {
			result = approvalService.getList(params);
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

	/**
	 * 법인카드 사용 내역 목록 조회
	 * @param seq: 검색 조건
	 * @param model: modelMap
	 */
	@RequestMapping(value = "/payhistList", method = RequestMethod.GET)
	public String getPayhistList(@RequestParam(value = "seq") long seq, Model model){
		HashMap<String, Object> result = null;
		try {
			result = approvalService.getPayhistList(seq);
		} finally {
			if (result != null && result.get("list") != null) {
				model.addAttribute("result", result);
				model.addAttribute("CODE", "SUCCESS");
				model.addAttribute("MSG", "결제내역 조회 성공.");
				logger.info("결제내역 조회 성공.");
			} else {
				model.addAttribute("CODE", "EMPTY");
				model.addAttribute("MSG", "결제내역 없음");
				logger.info("결제내역 없음");
			}
		}
		return "jsonView";
	}
}
