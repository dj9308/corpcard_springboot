package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.StateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateInfoRepository extends JpaRepository<StateInfo, Long> {

}
