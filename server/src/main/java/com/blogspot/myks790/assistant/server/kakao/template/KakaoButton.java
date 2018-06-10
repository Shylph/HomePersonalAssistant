package com.blogspot.myks790.assistant.server.kakao.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoButton {
    private String title;
    private KakaoLink link;
}
