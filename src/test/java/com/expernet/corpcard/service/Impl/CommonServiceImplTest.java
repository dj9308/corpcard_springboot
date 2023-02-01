package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.dto.common.TotalSumListDTO;
import com.expernet.corpcard.repository.CardUsehistRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class CommonServiceImplTest {

    @Autowired
    private CardUsehistRepository cardUsehistRepository;
    @Test
    public void searchTotalSumList() {
        String startYm = "2022-07";
        String endYm = "2023-01";

        List<TotalSumListDTO> test =
                cardUsehistRepository.selectSumGroupByUserId(null, startYm, endYm);

        assertNotNull(test);
    }
}