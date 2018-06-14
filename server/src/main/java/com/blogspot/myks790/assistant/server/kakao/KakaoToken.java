package com.blogspot.myks790.assistant.server.kakao;

import com.blogspot.myks790.assistant.server.security.Account;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
public class KakaoToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
