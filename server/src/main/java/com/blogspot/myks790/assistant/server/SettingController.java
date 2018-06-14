package com.blogspot.myks790.assistant.server;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/setting")
public class SettingController {

    @Value("${weather.api.key}")
    private String weatherKey;
    private final NoticeScheduler scheduler;
    @Autowired
    HomeInfoRepository homeInfoRepository;
    @Autowired
    EquipmentRepository equipmentRepository;
    @Autowired
    KakaoApi kakaoApi;

    @Autowired
    public SettingController(NoticeScheduler scheduler) {
        this.scheduler = scheduler;
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
    public String notice() {
        kakaoApi.sendToMeWithKakaotalk("알림 보내기~~~~");
        printWeather();
        return "/setting";
    }

    private void printWeather() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=" + weatherKey + "&base_date=20180614&base_time=0500&nx=60&ny=127&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=json");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String src = EntityUtils.toString(entity);
            log.info(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
