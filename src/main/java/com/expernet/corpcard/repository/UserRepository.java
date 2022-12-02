package com.expernet.corpcard.repository;

import com.expernet.corpcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(
            value = "ALTER TABLE TB_USER AUTO_INCREMENT = 1",
            nativeQuery = true
    )
    void resetIncrement();
}
