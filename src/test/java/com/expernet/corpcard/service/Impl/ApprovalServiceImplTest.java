package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public class ApprovalServiceImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeptRepository deptRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void searchApprovalList() {
        //given
        String userId = "53";
        //when
        User userInfo = userRepository.findByUserId(userId);
//        List<Dept> dept = deptRepository.findAll();
        //then
        assertNotNull(userInfo);
    }
}