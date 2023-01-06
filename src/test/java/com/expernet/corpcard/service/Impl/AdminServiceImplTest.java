package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import com.expernet.corpcard.repository.UserAddInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class AdminServiceImplTest {

    @Autowired
    private UserAddInfoRepository userAddInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void searchManagerList() {
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn("Y");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<UserDTO> test= modelMapper.map(userEntityList, new TypeToken<List<UserDTO>>() {}.getType());
        assertNotNull(test);
    }

    @Test
    public void updateAuth() {
        String adminYn = "N";
        List<String> userIdList = new ArrayList<>();
        userIdList.add("3");
        userIdList.add("405");

        List<UserAddInfo> addInfos = userAddInfoRepository.findAllByUser_UserIdIn(userIdList);
        List<UserAddInfo> test = userAddInfoRepository.findAll();

        for(UserAddInfo info : addInfos){
            info.setAdminYn(adminYn);
        }
        Object result = userAddInfoRepository.saveAll(addInfos);


//        List<User> addInfos = userRepository.findAllByUserIdIn(userIdList);
//
//        for(User info : addInfos){
//            info.setUserAddInfo(UserAddInfo.builder()
//                    .user(User.builder().userId(info.getUserId()).build())
//                    .adminYn("N").build());
//        }
//        Object result = userRepository.saveAll(addInfos);
        assertNotNull(result);
    }
}