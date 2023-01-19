package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    CardInfo findBySeq(Long seq);
    @Query(value = "SELECT ci FROM CardInfo ci LEFT JOIN CardReceiptent cr ON ci.seq = cr.cardInfo.seq " +
            "WHERE cr.user.userId = :userId " +
            "AND (function('date_format', cr.receivedAt, '%Y-%m') <= :wrtYm " +
            "AND (function('date_format', cr.returnedAt, '%Y-%m') >= :wrtYm OR cr.returnedAt IS NULL)) " +
            "GROUP BY ci.seq")
    List<CardInfo> findAllByUserIdAndReceivedAt(@Param("userId")String userId,
                                                @Param("wrtYm") String wrtYm);
}
