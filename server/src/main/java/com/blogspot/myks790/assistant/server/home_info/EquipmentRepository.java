package com.blogspot.myks790.assistant.server.home_info;

import com.blogspot.myks790.assistant.server.security.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("select equipment from Equipment equipment where equipment.account = :account")
    List<Equipment> findAll(@Param("account") Account account);
}
