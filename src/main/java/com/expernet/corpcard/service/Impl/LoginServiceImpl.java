package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 로그인 Service Implement Class
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
@Service("LoginService")
public class LoginServiceImpl implements LoginService {
    /**
     * User Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * load Security UserDetails
     * @param userId : user ID
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User userinfo = userRepository.findByUserId(userId);
        List<String> roles = new ArrayList<>();

        if (userinfo == null) {
            throw new UsernameNotFoundException("해당 사용자가 없습니다.");
        }
        if(userinfo.getChiefYn().toString().equals("Y")){
            roles.add("CHIEF");
        }
        if(userinfo.getUserAddInfo().getAdminYn().equals("Y")){
            roles.add("ADMIN");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(userId)
                .password(userinfo.getUserAddInfo().getUserPasswd())
                .roles(roles.toArray(new String[0])).build();
    }
}
