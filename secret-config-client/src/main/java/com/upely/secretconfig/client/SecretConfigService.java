package com.upely.secretconfig.client;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.upely.secretconfig.common.constants.CommonConstants;
import com.upely.secretconfig.common.util.HttpResult;
import com.upely.secretconfig.common.util.JacksonUtil;
import com.upely.secretconfig.common.util.Pbkdf2Util;

/**
 * @author dht31261
 * @date 2025年10月12日 21:16:36
 */
public final class SecretConfigService {

    private static final int ROOT_KEY_LENGTH = 32;
    private static final int ROOT_KEY_ITERATIONS = 100000;
    private static final int NOT_FOUND = 404;

    private static final String SECRET_CONFIG_DOMAIN = "http://" + System.getenv("SECRET_CONFIG_DOMAIN");

    static Map<String, String> secretConfig(String appName, int configType) {
        HttpResult<Map<String, String>> rlt = httpGet(
                SECRET_CONFIG_DOMAIN
                        + String.format("/secret-config/config?appName=%s&configType=%d", appName, configType),
                new TypeReference<>() {
                });
        if (!rlt.isSuccess()) {
            if (rlt.getCode() == NOT_FOUND) {
                return null;
            }
            throw new RuntimeException("get secret config error. code = " + rlt.getCode());
        }
        return rlt.getData();
    }

    static String workKey(String appName, int configType) {
        HttpResult<String> rlt = httpGet(
                SECRET_CONFIG_DOMAIN
                        + String.format("/secret-config/workKey?appName=%s&configType=%d", appName, configType),
                new TypeReference<>() {
                });
        if (!rlt.isSuccess()) {
            if (rlt.getCode() == NOT_FOUND) {
                return null;
            }
            throw new RuntimeException("get workkey error. code = " + rlt.getCode());
        }
        return rlt.getData();
    }

    static String rootKey() {
        HttpResult<List<String>> rtkRlt = httpGet(SECRET_CONFIG_DOMAIN + "/secret-config/allRtk",
                new TypeReference<>() {
                });
        if (!rtkRlt.isSuccess()) {
            throw new RuntimeException("get rtk error. code = " + rtkRlt.getCode());
        }
        HttpResult<String> rtsRlt = httpGet(SECRET_CONFIG_DOMAIN + "/secret-config/rts", new TypeReference<>() {
        });
        if (!rtsRlt.isSuccess()) {
            throw new RuntimeException("get rts error. code = " + rtsRlt.getCode());
        }
        List<byte[]> rtks = new ArrayList<>();
        rtks.add(Base64.getDecoder().decode(CommonConstants.ROOT_KEY_FACTOR));
        for (String iter : rtkRlt.getData()) {
            rtks.add(Base64.getDecoder().decode(iter));
        }
        return Pbkdf2Util.key(new String(Pbkdf2Util.xor(rtks)), Base64.getDecoder().decode(rtsRlt.getData()),
                ROOT_KEY_ITERATIONS, ROOT_KEY_LENGTH);
    }

    private static final <T> HttpResult<T> httpGet(String url, TypeReference<T> type) {
        Duration timeout = Duration.ofSeconds(30);
        HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).build();
        HttpResult<T> httpResult = new HttpResult<>();
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(timeout);
            HttpRequest req = builder.GET().build();
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            httpResult.setCode(response.statusCode());
            httpResult.setResponse(response);
            httpResult.setContent(response.body());
            System.out.printf("url = %s, method = GET, code = %d\n", url, response.statusCode());
            if (response.statusCode() >= HttpURLConnection.HTTP_OK
                    && response.statusCode() < HttpURLConnection.HTTP_MULT_CHOICE && type != null) {
                T result = JacksonUtil.toObject(response.body(), type);
                httpResult.setData(result);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return httpResult;
    }
}