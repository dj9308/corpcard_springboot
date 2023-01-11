package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long> {
    /**
     * 부서 조회
     * @param deptCd: 부서 code
     */
    Dept findByDeptCd(String deptCd);

    /**
     * 부서 목록 조회
     * @param upperCd: 상위 부서 code
     */
    List<Dept> findAllByUpper_deptCd(String upperCd);

    /**
     * 부서 이름 조회
     * @param upperCd: 상위 부서 code
     */
    @Query(value = "SELECT td.deptNm " +
            "FROM Dept td " +
            "WHERE td.upperDeptCd = :upperCd")
    List<String> findDeptNmByUpperCd(@Param("upperCd") String upperCd);
}
