package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.UsehistSubmitInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public interface UsehistSubmitInfoRepository extends JpaRepository<UsehistSubmitInfo, Long> {
    UsehistSubmitInfo findByWriterIdAndWrtYm(String writerId, String wrtYm);

    @Query(value = "SELECT usi " +
            "FROM UsehistSubmitInfo usi " +
            "WHERE usi.writerDept IN(:deptList) " +
            "AND (:writerNm is null or usi.writerNm = :writerNm) " +
            "AND usi.wrtYm BETWEEN :startDate AND :endDate")
    List<UsehistSubmitInfo> findByParams(@Param("deptList") List<String> deptList, @Param("writerNm") String writerNm,
                                         @Param("startDate") String startDate, @Param("endDate") String endDate);
}
