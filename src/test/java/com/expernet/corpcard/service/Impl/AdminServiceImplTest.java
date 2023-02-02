package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.admin.UserListDTO;
import com.expernet.corpcard.entity.CardInfo;
import com.expernet.corpcard.entity.CardReceiptent;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import com.expernet.corpcard.repository.CardInfoRepository;
import com.expernet.corpcard.repository.CardReceiptentRepository;
import com.expernet.corpcard.repository.UserAddInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
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
    @Autowired
    private CardReceiptentRepository cardReceiptentRepository;
    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Test
    public void searchManagerList() {
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn("Y");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<UserListDTO.Response> test = modelMapper.map(userEntityList, new TypeToken<List<UserListDTO.Response>>() {
        }.getType());
        assertNotNull(test);
    }

    @Test
    public void updateAuth() {
        String adminYn = "Y";
        List<String> userIdList = new ArrayList<>();
        userIdList.add("3");
        userIdList.add("405");

        List<User> test = userRepository.findAllByUserIdIn(userIdList);
        List<UserAddInfo> addInfos = new ArrayList<>();

        for (User user : test) {
            UserAddInfo info = user.getUserAddInfo();
            info.setAdminYn("Y");
            addInfos.add(info);
        }
        Object result = userAddInfoRepository.saveAll(addInfos);
        assertNotNull(result);
    }

    @Test
    public void insertReceiptent() {
        long seq = 3;
        User userinfo = User.builder()
                .seq(69)
                .userId("405")
                .ofcds("연구원")
                .userNm("설동재")
                .chiefYn('N')
                .build();
        CardInfo cardInfo = CardInfo.builder()
                .seq(14)
                .build();

        CardReceiptent test = CardReceiptent.builder()
                .user(userinfo)
                .cardInfo(cardInfo)
                .build();

        CardInfo ee = cardInfoRepository.findById(seq).orElse(null);
        assertNotNull(ee);
    }
}