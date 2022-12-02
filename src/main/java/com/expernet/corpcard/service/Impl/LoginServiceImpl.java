package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {

//    @Autowired
//    private LoginMapper loginMapper;

    @Override
    public Map actionLogin(User user) {
        Map<String, String> test = new HashMap<>();

        return test;
    }
}
