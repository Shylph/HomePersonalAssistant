package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Service
public class WeatherAPI {
    @Value("${weather.api.key}")
    private String weatherKey;
    private int x, y;

    public WeatherAPI() {
        x = 53;
        y = 37;
    }

    double forecastGribTemp() {
        Double result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        Date currentTime = new Date();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String baseDate = mSimpleDateFormat.format(currentTime);

        mSimpleDateFormat = new SimpleDateFormat("mm", Locale.KOREA);
        int mm = Integer.parseInt(mSimpleDateFormat.format(currentTime));
        mSimpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
        Integer HH = Integer.parseInt(mSimpleDateFormat.format(currentTime));
        if (mm < 40) {
            HH -= 1;
        }
        String baseTime = HH.toString() + "00";
        if (baseTime.length() == 3) {
            baseTime = "0" + baseTime;
        }
        log.info(" " + baseDate + "  " + baseTime);
        //초단기 실황
        HttpGet httpGet = new HttpGet("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib?serviceKey=" + weatherKey + "&base_date=" + baseDate + "&base_time=" + baseTime + "&nx=" + x + "&ny=" + y + "&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=json");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String src = EntityUtils.toString(entity);


            log.info(src);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(src);
                JSONObject response2 = (JSONObject) jsonObject.get("response");
                JSONObject body = (JSONObject) response2.get("body");
                JSONObject items = (JSONObject) body.get("items");
                JSONArray item = (JSONArray) items.get("item");
                JSONObject weatherInfo = (JSONObject) item.get(0);

                for (int i = 0; i < item.size(); i++) {
                    weatherInfo = (JSONObject) item.get(i);
                    if (weatherInfo.get("category").equals("T1H")) {//기온
                        result = Double.parseDouble(weatherInfo.get("obsrValue").toString());
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }

            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
