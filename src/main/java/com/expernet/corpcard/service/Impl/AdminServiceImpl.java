package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.ApprovalSearch;
import com.expernet.corpcard.dto.CardDTO;
import com.expernet.corpcard.dto.UserDTO;
import com.expernet.corpcard.entity.*;
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

import java.sql.Timestamp;
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
     * 카드 정보 Repository
     */
    @Autowired
    private final CardInfoRepository cardInfoRepository;

    /**
     * 카드 수령인 Repository
     */
    @Autowired
    private final CardReceiptentRepository cardReceiptentRepository;

    /**
     * 관리자 권한 조회
     *
     * @param paramMap : 사용자 정보
     */
    @Override
    public List<UserDTO> searchManagerList(HashMap<String, Object> paramMap) {
        //TODO JPA FETCH 전략 확인 및 조회 속도 개선 필요
        String adminYn = paramMap.get("adminYn").toString();
        List<User> userEntityList = userRepository.findAllByUserAddInfo_AdminYn(adminYn);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userEntityList, new TypeToken<List<UserDTO>>() {
        }.getType());
    }

    /**
     * 관리자 권한 변경
     *
     * @param paramMap : 권한 변경할 사용자 정보
     */
    @Override
    public Object updateAuth(HashMap<String, Object> paramMap) {
        String adminYn = paramMap.get("adminYn").toString();
        String listJSON = paramMap.get("userIdList").toString();
        List<String> userIdList;
        try {
            userIdList = new ObjectMapper().readValue(listJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }

        List<User> userInfoList = userRepository.findAllByUserIdIn(userIdList);
        List<UserAddInfo> addInfoList = new ArrayList<>();

        for (User user : userInfoList) {
            UserAddInfo info = user.getUserAddInfo();
            info.setAdminYn(adminYn);
            addInfoList.add(info);
        }

        return userAddInfoRepository.saveAllAndFlush(addInfoList);
    }

    /**
     * 카드 목록 조회
     */
    @Override
    public List<CardInfo> searchCardList() {
        return cardInfoRepository.findAll();
    }

    /**
     * 카드 정보 삭제
     *
     * @param paramMap : 카드 정보
     */
    @Override
    public long deleteCardInfo(HashMap<String, Object> paramMap) {
        //TODO cascade 이용한 자식 엔티티 삭제 처리 필요
        long result = -1;
        String listJSON = paramMap.get("cardSeqList").toString();
        List<Long> cardSeqList;
        try {
            cardSeqList = new ObjectMapper().readValue(listJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return -1;
        }
        int prevCnt = cardInfoRepository.findAll().size();
        cardReceiptentRepository.deleteAllByCardInfo_SeqIn(cardSeqList);
        cardInfoRepository.deleteAllByIdInBatch(cardSeqList);
        int afterCnt = cardInfoRepository.findAll().size();

        if (prevCnt - afterCnt == cardSeqList.size()) {
            result = cardSeqList.size();
        }
        return result;
    }

    /**
     * 카드 정보 저장 or 수정
     *
     * @param cardDTO : 카드 정보
     */
    @Override
    public CardInfo saveCardInfo(CardDTO cardDTO) {
        //TODO 저장 후 cardinfo onetomany column 조회 안되는 이유 확인 필요
        CardInfo result = null;

        //1.카드 정보 저장 or 수정
        CardInfo cardInfo = CardInfo.builder()
                .seq(cardDTO.getCardSeq())
                .cardComp(cardDTO.getCardComp())
                .cardNum(cardDTO.getCardNum())
                .build();

        result = cardInfoRepository.saveAndFlush(cardInfo);
        List<CardReceiptent> receiptents  = cardReceiptentRepository.findAllByCardInfo_Seq(cardDTO.getCardSeq());
        //2.카드 수령인 저장 or 수정(반납)
        if(!receiptents.isEmpty()){
            CardReceiptent receiptent = receiptents.get(receiptents.size()-1);
            if (receiptent.getReturnedAt() != null) {
                if(cardDTO.getUserId() != null){
                    saveReceiptentByParams(cardDTO, result);
                }
            } else {
                if (cardDTO.getUserId() == null) {
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                }else if(!cardDTO.getUserId().equals(receiptent.getUser().getUserId())){
                    receiptent.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                    cardReceiptentRepository.save(receiptent);
                    saveReceiptentByParams(cardDTO, result);
                }
            }
        }else{
            if(cardDTO.getUserId() != null){
                saveReceiptentByParams(cardDTO, result);
            }
        }

        return result;
    }

    /**
     * 사용자 전체 목록 조회
     */
    @Override
    public List<UserDTO> searchUserList() {
        List<User> userList = userRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userList, new TypeToken<List<UserDTO>>() {
        }.getType());
    }

    /**
     * 카드 수령인 정보 저장
     */
    private void saveReceiptentByParams(CardDTO cardDTO, CardInfo cardInfo){
        User userInfo = userRepository.findByUserId(cardDTO.getUserId());
        CardReceiptent cardReceiptent = CardReceiptent.builder()
                .cardInfo(cardInfo)
                .user(userInfo)
                .build();
        cardReceiptentRepository.save(cardReceiptent);
    }
}
