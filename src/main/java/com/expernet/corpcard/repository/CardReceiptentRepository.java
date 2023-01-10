package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardReceiptent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardReceiptentRepository extends JpaRepository<CardReceiptent, Long> {
    CardReceiptent findBySeq(long seq);

    @Transactional
    @Modifying
    @Query("delete from CardReceiptent cr where cr.cardInfo.seq in :seqList")
    void deleteAllByCardInfo_SeqIn(@Param("seqList") List<Long> seqList);
    List<CardReceiptent> findAllByCardInfo_Seq(long seq);
}

