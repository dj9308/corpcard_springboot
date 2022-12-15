package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.UsehistSubmitInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UsehistSubmitInfoRepository extends JpaRepository<UsehistSubmitInfo, Long> {
    UsehistSubmitInfo findByWriterIdAndWrtYm(String writerId, Date wrtYm);
}
