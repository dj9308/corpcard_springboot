package com.expernet.corpcard.repository;

import com.expernet.corpcard.dto.common.SearchTotalSumListDTO;
import com.expernet.corpcard.entity.CardUsehist;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface CardUsehistRepository extends JpaRepository<CardUsehist, Long> {
    /**
     * 결제 내역 조회
     * @param submitSeq : 제출정보 seq
     */
    List<CardUsehist> findAllByUsehistSubmitInfo_Seq(long submitSeq, Sort sort);

    /**
     * 결제 내역 조회
     * @param submitSeq : 제출정보 seq
     * @param classCd : 분류 code
     */
    @Query(value = "SELECT cu FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq = :submitSeq " +
            "AND (:classCd is null or cu.usehistSubmitInfo.stateInfo.stateCd = :classCd) ")
    List<CardUsehist> findAllBySubmitSeqAndClassCd(@Param("submitSeq") long submitSeq,
                                                   @Param("classCd") String classCd,
                                                   Sort sort);

    /**
     * 결제 내역 조회
     * @param seqList : 제출정보 seq List
     */
    @Query(value = "SELECT cu FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq IN (:seqList) " +
            "AND cu.usehistSubmitInfo.stateInfo.stateCd = 'C' ")
    List<CardUsehist> findAllByUserhistSubmitInfo_SeqIn(List<Long> seqList);

    /**
     * 분류별 결제 내역 합계 조회
     * @param submitSeq : 제출정보 seq
     */
    @Query(value = "SELECT new Map(cu.classInfo.seq AS seq, cu.classInfo.classNm AS classNm, SUM(cu.money) AS sum) " +
            "FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq = :submitSeq " +
            "AND (:classCd is null or cu.usehistSubmitInfo.stateInfo.stateCd = :classCd) " +
            "GROUP BY cu.classInfo.seq " +
            "ORDER BY cu.classInfo.seq ASC")
    List<HashMap<String, Object>> selectSumGroupByClassSeq(@Param("submitSeq") long submitSeq,
                                                           @Param("classCd") String classCd);

    /**
     * 분류별 결제 내역 합계 조회
     * @param seqList : 제출정보 seq List
     */
    @Query(value = "SELECT new Map(cu.classInfo.seq AS seq, cu.classInfo.classNm AS classNm, SUM(cu.money) AS sum) " +
            "FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq IN (:seqList) " +
            "AND cu.usehistSubmitInfo.stateInfo.stateCd = 'C' " +
            "GROUP BY cu.classInfo.seq " +
            "ORDER BY cu.classInfo.seq ASC")
    List<HashMap<String, Object>> selectSumGroupByClassSeqIn(@Param("seqList") List<Long> seqList);

    /**
     * 부서별 결제 내역 합계 조회
     * @param seqList : 제출정보 seq List
     */
    @Query(value = "SELECT new Map(cu.usehistSubmitInfo.writerDept AS team ,SUM(cu.money) AS sum) " +
            "FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq IN (:seqList) " +
            "AND cu.usehistSubmitInfo.stateInfo.stateCd = 'C' "+
            "GROUP BY cu.usehistSubmitInfo.writerDept")
    List<HashMap<String, Object>> selectSumDeptBySubmitSeqIn(@Param("seqList") List<Long> seqList);

    /**
     * 결제 내역 총계 조회
     * @param submitSeq : 제출정보 seq
     */
    @Query(value = "SELECT SUM(cu.money) AS sum FROM CardUsehist cu " +
            "WHERE cu.usehistSubmitInfo.seq  = :submitSeq " +
            "AND (:classCd is null or cu.usehistSubmitInfo.stateInfo.stateCd = :classCd) ")
    long selectTotalSumBySubmitSeq(@Param("submitSeq") long submitSeq,
                                   @Param("classCd") String classCd);

    /**
     * 월별 총계 조회
     * @param userId    : 사용자 ID
     * @param startYm   : 검색조건 시작 연월
     * @param endYm     : 검색조건 마지막 연월
     */
    @Query(value = "SELECT new com.expernet.corpcard.dto.common.SearchTotalSumListDTO( " +
            "cu.usehistSubmitInfo.seq, cu.usehistSubmitInfo.wrtYm, sum(cu.money)) " +
            "FROM CardUsehist cu " +
            "WHERE (:userId is null or cu.usehistSubmitInfo.user.userId = :userId) " +
            "AND cu.usehistSubmitInfo.wrtYm BETWEEN :startYm AND :endYm " +
            "AND cu.usehistSubmitInfo.stateInfo.stateCd = 'C' " +
            "GROUP BY cu.usehistSubmitInfo.wrtYm " +
            "ORDER BY cu.usehistSubmitInfo.wrtYm ASC")
    List<SearchTotalSumListDTO> selectSumGroupByUserId(@Param("userId") String userId,
                                        @Param("startYm") String startYm,
                                        @Param("endYm") String endYm);
}
