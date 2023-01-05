package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

}
