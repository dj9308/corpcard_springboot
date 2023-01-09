package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.ApprovalSearch;
import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.AdminService;
import com.expernet.corpcard.service.ApprovalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertNotNull;

@Transactional
@RequiredArgsConstructor
@Service("AdminService")
public class AdminServiceImpl implements AdminService {
    /**
     * 사용자 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 추가 정보 Repository
     */
    @Autowired
    private UserAddInfoRepository userAddInfoRepository;

    /**
     * 관리자 권한 조회
     * @param paramMap : 사용자 정보
     */
    @Override
    public List<UserDTO> searchManagerList(HashMap<String, Object> paramMap) {
        //TODO JPA FETCH 전략 확인 및 조회 속도 개선 필요
        String adminYn = paramMap.get("adminYn").toString();
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn(adminYn);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userEntityList, new TypeToken<List<UserDTO>>() {}.getType());
    }

    /**
     * 관리자 권한 변경
     * @param paramMap : 권한 변경할 사용자 정보
     */
    @Override
    public Object updateAuth(HashMap<String, Object> paramMap) {
        String adminYn = paramMap.get("adminYn").toString();
        String listJSON = paramMap.get("userIdList").toString();
        List<String> userIdList;
        try {
            userIdList = new ObjectMapper().readValue(listJSON, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return null;
        }

        List<User> userInfoList = userRepository.findAllByUserIdIn(userIdList);
        List<UserAddInfo> addInfoList = new ArrayList<>();

        for(User user : userInfoList){
            UserAddInfo info = user.getUserAddInfo();
            info.setAdminYn(adminYn);
            addInfoList.add(info);
        }

        return userAddInfoRepository.saveAll(addInfoList);
    }
}
