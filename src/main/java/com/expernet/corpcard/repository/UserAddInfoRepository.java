package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddInfoRepository extends JpaRepository<UserAddInfo, Long> {
    List<UserAddInfo> findAllByUser_UserIdIn(List<String> userIdList);
}