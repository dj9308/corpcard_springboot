package com.expernet.corpcard.controller;

import com.expernet.corpcard.BaseController;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 로그인 Controller Class
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
public class LoginController extends BaseController {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 로그인 Service
     */
    @Resource(name = "LoginService")
    private LoginService loginService;

    /**
     * 로그인 페이지 접근
     */
    @RequestMapping(value = "/viewLogin", method = RequestMethod.GET)
    public String viewLogin() {
        HttpSession session = getSession();
        User userInfo = (User) session.getAttribute("USER_INFO");

        if (userInfo == null) return "login";
        else return "redirect:/payhist";
    }
}
