package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface CardUsehistRepository extends JpaRepository<CardUsehist, Long> {
    List<CardUsehist> findAllByUsehistSubmitInfo_Seq(long submitSeq);

    @Query(value = "SELECT new Map(cu.classInfo.seq AS seq, cu.classInfo.classNm AS classNm, SUM(cu.money) AS sum) " +
            "FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq = :submitSeq " +
            "GROUP BY cu.classInfo.seq " +
            "ORDER BY cu.classInfo.seq ASC")
    List<HashMap<String, Object>> selectSumGroupByClassSeq(@Param("submitSeq") long submitSeq);

    @Query(value = "SELECT SUM(money) AS sum FROM card_usehist " +
            "WHERE submit_seq  = :submitSeq", nativeQuery = true)
    long selectTotalSumBySubmitSeq(@Param("submitSeq") long submitSeq);

    @Query(value = "SELECT new Map(cu.usehistSubmitInfo.seq AS seq, cu.usehistSubmitInfo.wrtYm AS wrtYm, " +
            "sum(cu.money) AS sum) " +
            "FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.user.userId = :userId " +
            "AND cu.usehistSubmitInfo.wrtYm BETWEEN :startYm AND :endYm " +
            "AND cu.usehistSubmitInfo.stateInfo.stateCd = 'C' " +
            "GROUP BY cu.usehistSubmitInfo.wrtYm " +
            "ORDER BY cu.usehistSubmitInfo.wrtYm ASC")
    List<HashMap<String, Object>> selectSumGroupByWrtYm(@Param("userId") String userId,
                                                        @Param("startYm") String startYm,
                                                        @Param("endYm") String endYm);
}
