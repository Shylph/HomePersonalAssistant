package com.blogspot.myks790.assistant.server.home_info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeInfoRepository extends JpaRepository<HomeInfo, Long> {


    @Query("select homeInfo from HomeInfo homeInfo")
    List<HomeInfo> findAllById(@Param("equipment") Equipment equipment);
}
