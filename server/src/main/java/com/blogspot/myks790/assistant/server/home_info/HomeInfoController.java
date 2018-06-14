package com.blogspot.myks790.assistant.server.home_info;

import com.blogspot.myks790.assistant.server.security.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/home_info")
public class HomeInfoController {
    @Autowired
    EquipmentRepository equipmentRepository;
    @Autowired
    HomeInfoRepository homeInfoRepository;

    @PostMapping("/register")
    public void register(@RequestBody Equipment equipment,UserAuthentication authentication) {
        log.info("register");
        equipment.setAccount(authentication.getAccount());
        equipmentRepository.save(equipment);
    }

    @GetMapping("/equipment_list")
    public List<Equipment> equipmentList(UserAuthentication authentication) {
        log.info("equipment_list");
        return equipmentRepository.findAll(authentication.getAccount());
    }

    @GetMapping("/info_list")
    public List<HomeInfo> infoList(@RequestParam(value = "equipment_id") Long equipmentId) {
        log.info("info_list");
        Equipment equipment = equipmentRepository.getOne(equipmentId);
        return homeInfoRepository.findAllById(equipment);
    }
}
