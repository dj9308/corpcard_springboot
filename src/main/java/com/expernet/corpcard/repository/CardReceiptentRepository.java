package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardReceiptent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardReceiptentRepository extends JpaRepository<CardReceiptent, Long> {

}
