package com.expernet.corpcard.service;


import com.expernet.corpcard.entity.User;

import java.util.Map;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 로그인 Service Class
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
public interface LoginService {
    Map actionLogin(User user);
}
