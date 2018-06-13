package com.blogspot.myks790.assistant.server;

import com.blogspot.myks790.assistant.server.home_info.HomeInfo;
import com.blogspot.myks790.assistant.server.home_info.HomeInfoRepository;
import com.blogspot.myks790.assistant.server.kakao.KakaoToken;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoButton;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoLink;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoMessageTemplate;
import com.blogspot.myks790.assistant.server.security.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/setting")
public class SettingController {
    @Value("${kakao.client.id}")
    private String clientId;
    private String redirectUrl;
    private KakaoToken kakaoToken;
    private final NoticeScheduler scheduler;
    @Autowired
    HomeInfoRepository homeInfoRepository;

    @Autowired
    public SettingController(NoticeScheduler scheduler) {
        this.scheduler = scheduler;
        redirectUrl = "http://localhost:8080/setting/kakao/callback";
    }

    @GetMapping("kakao/connect")
    public String kakaoConnect() {
        String url = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUrl +
                "&response_type=code";
        return "redirect:" + url;
    }

    @GetMapping("kakao/callback")
    public String kakaoCallback(@RequestParam(value = "code") String code, HttpSession session) {
        log.info("kakao login callback");
        log.info("kakao login code : " + code);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://kauth.kakao.com/oauth/token");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nvps.add(new BasicNameValuePair("client_id", clientId));
        nvps.add(new BasicNameValuePair("redirect_uri", redirectUrl));
        nvps.add(new BasicNameValuePair("code", code));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String src = EntityUtils.toString(entity);
            kakaoToken = new ObjectMapper().readerFor(KakaoToken.class).readValue(src);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "/setting";
    }

    @GetMapping("/collectT_H")
    public String collect() {
        Runnable runnable = () -> {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://192.168.1.178:8090/sensor/resource");
            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String src = EntityUtils.toString(entity);
                HomeInfo  homeInfo = new ObjectMapper().readerFor(HomeInfo.class).readValue(src);
                homeInfo = homeInfo;
                Account account = new Account();
                account.setAccount_id(17l);
                homeInfo.setAccount(account);
                homeInfoRepository.save(homeInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        scheduler.startScheduler(runnable, new CronTrigger("* 1 * * * *"));
        return "/setting";
    }

    @GetMapping("/notice")
    public String notice() {
        sendToMeWithKakaotalk("알림 보내기~~~~");
        return "/setting";
    }

    private void sendToMeWithKakaotalk(String message) {
        log.info("sendToMeWithKakaotalk");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://kapi.kakao.com/v2/api/talk/memo/default/send");
        List<NameValuePair> nvps = new ArrayList<>();
        String value = kakaoToken.getToken_type() + " " + kakaoToken.getAccess_token();
        httpPost.setHeader("Authorization", value);
        KakaoLink kakaoLink = new KakaoLink("http://localhost:8080");
        KakaoButton kakaoButton = new KakaoButton("되라", kakaoLink);
        KakaoMessageTemplate kakaoMessageTemplate = new KakaoMessageTemplate("text", message, kakaoLink);
        kakaoMessageTemplate.addButton(kakaoButton);

        try {
            String result = new ObjectMapper().writeValueAsString(kakaoMessageTemplate);
            nvps.add(new BasicNameValuePair("template_object", result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            response = httpclient.execute(httpPost);
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
