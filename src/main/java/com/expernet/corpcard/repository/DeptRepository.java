package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long> {
    Dept findByDeptCd(String deptCd);

    @Query(value = "SELECT td.deptNm " +
            "FROM Dept td " +
            "WHERE td.upperDeptCd = :upperCd")
    List<String> findDeptNmByUpperCd(@Param("upperCd") String upperCd);
}
