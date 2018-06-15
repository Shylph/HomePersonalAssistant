package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Controller
public class VoiceGuidance {
    @Value("${css.client.id}")
    String clientId;//애플리케이션 클라이언트 아이디값";
    @Value("${css.client.secret}")
    String clientSecret;//애플리케이션 클라이언트 시크릿값";
    private static String filename;

    public void createVoice(String preText, HttpServletRequest request) {
        filename = "voiceGuidance.mp3";
        String path = request.getServletContext().getRealPath("/");

        log.info(" create: " + path + " :voiceGuidance: " + preText);
        try {
            String spped = "0";
            String speaker = "jinho";
            //clara : 영어, 여성 음색
            //matt : 영어, 남성 음색
            //mijin : 한국어, 여성 음색
            //jinho : 한국어, 남성 음색
            String text = URLEncoder.encode(preText, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            // post request
            String postParams = "speaker=" + speaker + "&speed=" + spped + "&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];


                File f = new File(path + filename);
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                log.info(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @GetMapping("/voice")
    public ModelAndView get() {
        return new ModelAndView("voice", "path", filename);
    }
}
