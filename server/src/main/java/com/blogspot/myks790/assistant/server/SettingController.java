package com.blogspot.myks790.assistant.server;

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

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/setting")
public class SettingController {
    private final NoticeScheduler scheduler;
    private final HomeInfoRepository homeInfoRepository;
    private final KakaoApi kakaoApi;
    private final WeatherAPI weatherAPI;

    @Autowired
    public SettingController(NoticeScheduler scheduler, HomeInfoRepository homeInfoRepository, KakaoApi kakaoApi, WeatherAPI weatherAPI) {
        this.scheduler = scheduler;
        this.homeInfoRepository = homeInfoRepository;
        this.kakaoApi = kakaoApi;
        this.weatherAPI = weatherAPI;
    }


    @GetMapping("/collectT_H")
    public String collect(UserAuthentication authentication) {
        Runnable runnable = () -> {
            log.info("collectT_H runnable");
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://192.168.1.98:8090/sensor/resource");
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
        };

        scheduler.startScheduler(runnable, new CronTrigger("0 */1 * * * *"));
        return "setting";
    }

    @GetMapping("/notice")
    public String notice(UserAuthentication authentication) {
        PageRequest pageRequest =PageRequest.of(1,1, Sort.Direction.DESC,"timestamp");
        Page<HomeInfo> homes =  homeInfoRepository.findAll(pageRequest);
        HomeInfo info = homes.getContent().get(0);
        Double currentTemp = weatherAPI.forecastGribTemp();
        String message = "현재 방안의 온도는 " + info.getTemperature() + "도 입니다."+
                "밖의 기온은 "+currentTemp+"도 입니다.";
        boolean b = kakaoApi.sendToMeWithKakaotalk(message,authentication);
        if(b == false){
            //실패시 아무 안내 말 없이 그냥 카톡 인증 창으로 이동
            return "redirect:/kakao/connect";
        }
        return "/setting";
    }

    @GetMapping("/weather/notify")
    public String notifyWeather(@RequestParam(value = "notificationTime") int notificationTime) {
        Runnable runnable = () -> {

        };
        scheduler.registerWeatherNotification(runnable,notificationTime);
        return "/setting";
    }

    @GetMapping("/weather/unnotify")
    public String unnotifyWeather() {
        log.info("unno");
        scheduler.unregisterWeatherNotification();
        return "/setting";
    }

}
