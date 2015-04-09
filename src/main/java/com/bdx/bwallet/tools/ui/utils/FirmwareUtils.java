/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Administrator
 */
public class FirmwareUtils {

    final static String BASE_URL = "http://mybwallet.com";

    public static String getNewestURL() {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(BASE_URL + "/data/firmware/releases.json");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);

            List<Map<String, Object>> list = mapper.readValue(response.getEntity().getContent(), 
                    new TypeReference<List<LinkedHashMap<String, Object>>>() {});
            Map<String, Object> newest = null;
            if (list != null && list.size() > 0) {
                for (Map<String, Object> release : list) {
                    if (newest == null)
                        newest = release;
                    else {
                        if (compare((List<Integer>)newest.get("version"), (List<Integer>)release.get("version")) == -1)
                            newest = release;
                    }
                }
            }
            EntityUtils.consume(response.getEntity());
            if (newest == null)
                throw new RuntimeException("No firmware available");
            else 
                return BASE_URL + (String)newest.get("url");
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
            }
        }
    }

    private static int compare(List<Integer> version1, List<Integer> version2) {
        version1 = pad(version1);
        version2 = pad(version2);
        for (int i = 0; i < 3; i++) {
            int v1 = version1.get(i);
            int v2 = version2.get(i);
            if (v1 > v2)
                return 1;
            else if (v1 < v2)
                return -1;
        }
        return 0;
    }
    
    private static List<Integer> pad(List<Integer> version) {
        if (version ==  null)
            version = new ArrayList();
        for (int i = 0; i < 3 - version.size(); i++) {
            version.add(0);
        }
        return version;
    }
    
}
