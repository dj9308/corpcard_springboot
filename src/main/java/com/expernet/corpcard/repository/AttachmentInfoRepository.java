package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.AttachmentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentInfoRepository extends JpaRepository<AttachmentInfo, Long> {

}
