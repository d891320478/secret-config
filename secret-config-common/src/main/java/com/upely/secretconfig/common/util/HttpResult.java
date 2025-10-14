package com.upely.secretconfig.common.util;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author dht31261
 * @date 2025年10月12日 21:16:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HttpResult<T> {

    public static final int HTTP_THROWABLE_CODE = -601;

    private int code;
    private T data;
    private String content;
    @JsonIgnore
    @ToString.Exclude
    private HttpResponse<?> response;
    private long costTime;

    public boolean isSuccess() {
        return code >= HttpURLConnection.HTTP_OK && code < HttpURLConnection.HTTP_MULT_CHOICE;
    }
}
