package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    CardInfo findBySeq(Long seq);

    @Query(value = "SELECT ci FROM CardInfo ci LEFT JOIN CardReceiptent cr ON ci.seq = cr.cardInfo.seq " +
            "WHERE cr.user.userId = :userId " +
            "AND cr.receivedAt >= :startDate AND cr.receivedAt <= :endDate " +
            "GROUP BY ci.seq")
    List<CardInfo> findAllByUserIdAndReceivedAt(@Param("userId")String userId,
                                                @Param("startDate") Timestamp startDate,
                                                @Param("endDate")Timestamp endDate);
}
