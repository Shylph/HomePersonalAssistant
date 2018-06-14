package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Slf4j
@Service
public class WeatherAPI {
    @Value("${weather.api.key}")
    private String weatherKey;

    void printWeather() {
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
