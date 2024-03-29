package com.expernet.corpcard.repository;

import com.expernet.corpcard.dto.common.ApprovalDTO;
import com.expernet.corpcard.entity.UsehistSubmitInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface UsehistSubmitInfoRepository extends JpaRepository<UsehistSubmitInfo, Long> {
    UsehistSubmitInfo findByWriterIdAndWrtYm(String writerId, String wrtYm);

    List<UsehistSubmitInfo> findByWrtYm(String wrtYm);

    UsehistSubmitInfo findBySeq(long seq);

    @Query(value = "SELECT new Map(usi.seq AS seq, usi.stateInfo AS stateInfo, usi.writerDept AS writerDept, " +
            "usi.writerTeam AS writerTeam, usi.writerOfcds AS writerOfcds, usi.writerNm AS writerNm, " +
            "usi.wrtYm AS wrtYm, usi.user.userId AS userId, SUM(cu.money) AS sum) " +
            "FROM UsehistSubmitInfo usi LEFT JOIN CardUsehist cu ON usi.seq = cu.usehistSubmitInfo.seq " +
            "WHERE (:#{#approvalSearch.teamList} is null or usi.writerTeam IN (:#{#approvalSearch.teamList})) " +
            "AND (:#{#approvalSearch.deptList} is null or usi.writerTeam IN (:#{#approvalSearch.deptList})) " +
            "AND usi.stateInfo.stateCd != 'A' " +
            "AND (:#{#approvalSearch.writerNm} is null or usi.writerNm LIKE %:#{#approvalSearch.writerNm}%) " +
            "AND usi.wrtYm BETWEEN :#{#approvalSearch.startDate} AND :#{#approvalSearch.endDate} " +
            "GROUP BY usi.seq " +
            "ORDER BY usi.wrtYm DESC")
    List<HashMap<String, Object>> findByParams(@Param("approvalSearch") ApprovalDTO approvalSearch);
}
