package com.blogspot.myks790.assistant.server.kakao.template;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KakaoMessageTemplate {
    private String object_type;
    private String text;
    private KakaoLink link;
    private List<KakaoButton> buttons;


    public KakaoMessageTemplate(String objectType, String text,KakaoLink link) {
        this.object_type = objectType;
        this.text = text;
        this.link = link;
        buttons = new ArrayList<>();
    }
    public void addButton(KakaoButton button){
        buttons.add(button);
    }
}
