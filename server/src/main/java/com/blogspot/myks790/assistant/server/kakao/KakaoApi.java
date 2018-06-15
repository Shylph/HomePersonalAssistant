package com.blogspot.myks790.assistant.server.kakao;

import com.blogspot.myks790.assistant.server.kakao.template.KakaoButton;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoLink;
import com.blogspot.myks790.assistant.server.kakao.template.KakaoMessageTemplate;
import com.blogspot.myks790.assistant.server.security.UserAuthentication;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class KakaoApi {
    @Value("${kakao.client.id}")
    private String clientId;
    private String redirectUrl;
    private KakaoToken kakaoToken;

    private final KakaoTokenRepository kakaoTokenRepository;
    private String host;

    @Autowired
    public KakaoApi(KakaoTokenRepository kakaoTokenRepository) {
        redirectUrl = "http://localhost:8080/kakao/callback";
        this.kakaoTokenRepository = kakaoTokenRepository;
    }

    @GetMapping("kakao/connect")
    public String kakaoConnect() {
        String url = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUrl +
                "&response_type=code";
        return "redirect:" + url;
    }

    void getKakaoToken(@RequestParam(value = "code") String code, UserAuthentication authentication) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://kauth.kakao.com/oauth/token");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
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
            kakaoToken.setAccount(authentication.getAccount());
            kakaoTokenRepository.save(kakaoToken);
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
    }

    public boolean sendToMeWithKakaotalk(String message, UserAuthentication authentication) {
        host = "192.168.137.1";
        if (kakaoToken == null) {
            KakaoToken token = loadToken(authentication);
            if (token == null) {
                return false;
            }
        }
        log.info("sendToMeWithKakaotalk");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://kapi.kakao.com/v2/api/talk/memo/default/send");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String value = kakaoToken.getToken_type() + " " + kakaoToken.getAccess_token();
        httpPost.setHeader("Authorization", value);
        KakaoLink kakaoLink = new KakaoLink("http://" + host + ":8080/voice?voice=true");
        KakaoButton kakaoButton = new KakaoButton("음성으로 듣기", kakaoLink);
        kakaoLink = new KakaoLink("http://" + host + ":8080");
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
        return true;
    }

    private KakaoToken loadToken(UserAuthentication authentication) {
        KakaoToken token = null;
        try {
            if(kakaoToken != null) {
                token = kakaoTokenRepository.getOne(kakaoToken.getId());
                token.getAccess_token();
            }
        } catch (EntityNotFoundException e) {
            token = null;
        }
        return token;
    }

    @GetMapping("kakao/callback")
    public String kakaoCallback(@RequestParam(value = "code") String code, HttpSession session, UserAuthentication authentication) {
        log.info("kakao login callback");
        log.info("kakao login code : " + code);

        getKakaoToken(code, authentication);

        return "/setting";
    }
}
