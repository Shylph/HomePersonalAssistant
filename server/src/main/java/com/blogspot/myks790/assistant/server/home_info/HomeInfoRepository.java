package com.blogspot.myks790.assistant.server.home_info;

import com.blogspot.myks790.assistant.server.security.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeInfoRepository extends JpaRepository<HomeInfo, Long> {
    @Query("select homeInfo from HomeInfo homeInfo where homeInfo.account = :account")
    List<HomeInfo> findAll(@Param("account") Account account);
}
