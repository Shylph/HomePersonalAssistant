package com.blogspot.myks790.assistant.server.home_info;

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

    @PostMapping("/register")
    public void register(@RequestBody Equipment equipment) {
        log.info("register");
        equipmentRepository.save(equipment);
    }

    @GetMapping("/equipment_list")
    public List<Equipment> equipmentList() {
        log.info("equipment_list");
        return equipmentRepository.findAll();
    }
}
