package com.upely.secretconfig.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upely.secretconfig.constants.Constants;
import com.upely.secretconfig.domain.db.SecretConfigDO;
import com.upely.secretconfig.domain.db.WorkKeyDO;
import com.upely.secretconfig.service.SecretConfigService;
import com.upely.secretconfig.service.WorkKeyService;

import jakarta.annotation.Resource;

/**
 * @author dht31261
 * @date 2025年10月12日 20:13:13
 */
@RestController
@RequestMapping("/secret")
public class SecretController {

    @Resource
    private WorkKeyService workKeyService;
    @Resource
    private SecretConfigService secretConfigService;

    @GetMapping("/allRtk")
    public List<String> getAllRtk() throws IOException {
        String rtk = new String(Files.readAllBytes(Paths.get(Constants.RTK_FILE)));
        return Arrays.asList(rtk);
    }

    @GetMapping("/rts")
    public String getRts() throws IOException {
        return new String(Files.readAllBytes(Paths.get(Constants.RTS_FILE)));
    }

    @GetMapping("/workKey")
    public ResponseEntity<String> workKey(String appName, int configType) {
        WorkKeyDO wk = workKeyService.getByAppNameAndConfigType(appName, configType);
        if (wk == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wk.getWorkKey());
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> config(String appName, int configType) {
        List<SecretConfigDO> list = secretConfigService.selectListByAppNameAndConfigType(appName, configType);
        if (list == null || list.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> map = new HashMap<>();
        for (SecretConfigDO iter : list) {
            map.put(iter.getConfigKey(), iter.getConfigValue());
        }
        return ResponseEntity.ok(map);
    }
}