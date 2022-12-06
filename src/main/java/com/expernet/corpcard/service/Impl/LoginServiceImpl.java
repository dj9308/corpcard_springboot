package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("LoginService")
public class LoginServiceImpl implements LoginService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map actionLogin(User user) {
        Map<String, String> test = new HashMap<>();

        return test;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User userinfo = userRepository.findByUserId(userId);
        return null;
//        return new org.springframework.security.core.userdetails.User(userinfo.getUserId()
//                , userinfo.getUserAddInfo().getUserPasswd(), );
    }
}
