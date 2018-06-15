package com.blogspot.myks790.assistant.server.kakao.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoLink {
    String web_url;
    String mobile_web_url;
    public KakaoLink(String url){
        web_url=url;
        mobile_web_url=url;
    }
}
