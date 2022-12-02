package com.expernet.corpcard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Base Controller Class
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
public class BaseController {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * request객체 반환
     *
     * @return HttpServletRequest
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * session 반환
     *
     * @return HttpSession
     */
    protected HttpSession getSession() {
        HttpServletRequest req = getRequest();
        return req.getSession();
    }
}
