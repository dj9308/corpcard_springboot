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

    @Override
    public List<UserDTO> searchManagerList() {
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn("Y");
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

        List<UserAddInfo> addInfos = userAddInfoRepository.findAllByUser_UserIdIn(userIdList);

        for(UserAddInfo info : addInfos){
            info.setAdminYn(adminYn);
        }
        return userAddInfoRepository.saveAll(addInfos);
    }
}
