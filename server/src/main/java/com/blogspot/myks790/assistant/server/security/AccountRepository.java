package com.blogspot.myks790.assistant.server.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.username = :username")
    Account findByUsername(@Param("username") String userId);
}
