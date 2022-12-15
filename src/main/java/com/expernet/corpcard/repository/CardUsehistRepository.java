package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.CardUsehist;
import com.expernet.corpcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardUsehistRepository extends JpaRepository<CardUsehist, Long> {
    List<CardUsehist> findBySubmitSeq(long submitSeq);
}
