package com.blogspot.myks790.assistant.server;

import com.blogspot.myks790.assistant.server.home_info.Equipment;
import com.blogspot.myks790.assistant.server.home_info.EquipmentRepository;
import com.blogspot.myks790.assistant.server.home_info.HomeInfo;
import com.blogspot.myks790.assistant.server.home_info.HomeInfoRepository;
import com.blogspot.myks790.assistant.server.kakao.KakaoApi;
import com.blogspot.myks790.assistant.server.security.UserAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/setting")
public class SettingController {
    private final NoticeScheduler scheduler;
    private final HomeInfoRepository homeInfoRepository;
    private final KakaoApi kakaoApi;
    private final WeatherAPI weatherAPI;
    private final VoiceGuidance voiceGuidance;
    private final EquipmentRepository equipmentRepository;

    @Autowired
    public SettingController(NoticeScheduler scheduler, HomeInfoRepository homeInfoRepository, KakaoApi kakaoApi, WeatherAPI weatherAPI, VoiceGuidance voiceGuidance, EquipmentRepository equipmentRepository) {
        this.scheduler = scheduler;
        this.homeInfoRepository = homeInfoRepository;
        this.kakaoApi = kakaoApi;
        this.weatherAPI = weatherAPI;
        this.voiceGuidance = voiceGuidance;
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping("/notice")
    public String notice(UserAuthentication authentication, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(1, 1, Sort.Direction.DESC, "timestamp");
        Page<HomeInfo> homes = homeInfoRepository.findAll(pageRequest);
        HomeInfo info = homes.getContent().get(0);
        Double currentTemp = weatherAPI.forecastGribTemp();
        String message = "현재 방안의 온도는 " + info.getTemperature() + "도 입니다." +
                "밖의 기온은 " + currentTemp + "도 입니다.";
        boolean b = kakaoApi.sendToMeWithKakaotalk(message, authentication);
        if (!b) {
            //실패시 아무 안내 말 없이 그냥 카톡 인증 창으로 이동
            return "redirect:/kakao/connect";
        }
        voiceGuidance.createVoice(message, request);
        return "/setting";
    }

    @GetMapping("/weather/notify")
    public String notifyWeather(@RequestParam(value = "notificationTime") int notificationTime) {
        Runnable runnable = () -> {

        };
        scheduler.registerWeatherNotification(runnable, notificationTime);
        return "/setting";
    }

    @GetMapping("/weather/unnotify")
    public String unnotifyWeather() {
        log.info("unno");
        scheduler.unregisterWeatherNotification();
        return "/setting";
    }

    @GetMapping("/collect")
    public String collect(@RequestParam(value = "collectTerm") int collectTerm, UserAuthentication authentication) {
        Runnable runnable = () -> {
            log.info("collectT_H runnable");
            List<Equipment> equipmentList = equipmentRepository.findAll(authentication.getAccount());
            for(Equipment equipment : equipmentList) {
                String ip = equipment.getIp();
                int port = equipment.getPort();
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet("http://" + ip + ":" + port + "/sensor/resource");
                CloseableHttpResponse response = null;
                try {
                    response = httpclient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String src = EntityUtils.toString(entity);
                    HomeInfo homeInfo = new ObjectMapper().readerFor(HomeInfo.class).readValue(src);
                    //homeInfo.setEquipment(equipmentRepository.getOne(1L));
                    homeInfoRepository.save(homeInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        scheduler.registerT_H_Collector(runnable, collectTerm);
        return "/setting";
    }

    @GetMapping("/stopCollect")
    public String stopCollect() {
        scheduler.unregisterT_H_Collector();
        return "/setting";
    }

}
