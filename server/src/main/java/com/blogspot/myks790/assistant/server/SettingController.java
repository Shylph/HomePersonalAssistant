package com.blogspot.myks790.assistant.server;

import com.blogspot.myks790.assistant.server.kakao.KakaoToken;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoButton;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoLink;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoMessageTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
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

    public SettingController(){
        redirectUrl = "http://localhost:8080/setting/kakao/callback";
    }

    @GetMapping("kakao/connect")
    public String kakaoConnect() {

        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?client_id=");
        url.append(clientId);
        url.append("&redirect_uri=");
        url.append(redirectUrl);
        url.append("&response_type=code");
        return "redirect:" + url.toString();
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
        sendMyKakaotack();
        return "/setting";
    }

    public void sendMyKakaotack() {
        log.info("sendMyKakaotack");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://kapi.kakao.com/v2/api/talk/memo/default/send");
        List<NameValuePair> nvps = new ArrayList<>();
        String value = kakaoToken.getToken_type() + " " + kakaoToken.getAccess_token();
        httpPost.setHeader("Authorization", value);
        KakaoLink kakaoLink = new KakaoLink("http://localhost:8080");
        KakaoButton kakaoButton = new KakaoButton("되라",kakaoLink);
        KakaoMessageTemplate kakaoMessageTemplate = new KakaoMessageTemplate("text","텍스트 영역입니다. 최대 200자 표시 가능합니다.",kakaoLink);
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
