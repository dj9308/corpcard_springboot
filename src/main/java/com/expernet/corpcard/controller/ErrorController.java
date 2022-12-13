package com.expernet.corpcard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Error Controller Class
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
public class ErrorController extends BaseController {

    /**
     * error 페이지 이동
     */
    @RequestMapping(value = "/error/{errorType}")
    public String error(@PathVariable String errorType, Model model) {
        return "error/403";
    }
}
