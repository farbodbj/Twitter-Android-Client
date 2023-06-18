package com.twitter.client.APIClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.twitter.common.API.API;
import com.twitter.common.API.ResponseModel;
import com.twitter.common.API.StatusCode;
import com.twitter.common.Utils.GsonUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ClientHttpUtils {
    private final static Gson GSON = GsonUtils.getInstance();

    public static <T> ResponseModel<T> get(
            String path,
            Map<String, String> query,
            Class<T> contentType,
            ErrorCallback onError
    ) {
        return execute("GET", path, null, query, null,onError, contentType);
    }

    public static <T> ResponseModel<T> get(
            String path,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            ErrorCallback onError
    ) {
        return execute("GET", path, null, query, headers, onError, contentType);
    }

    public static <T> ResponseModel<T> post(
            String path,
            Object data,
            Class<T> contentType,
            ErrorCallback onError
    ) {
        return execute("POST", path, data, null, null ,onError, contentType);
    }

    public static <T> ResponseModel<T> post(
            String path,
            Object data,
            Headers headers,
            Class<T> contentType,
            ErrorCallback onError
    ) {
        return execute("POST", path, data, null, headers ,onError, contentType);
    }

    public static <T> ResponseModel<T> postSerialized(
            String path,
            Serializable data,
            Headers headers,
            Class<T> contentType,
            ErrorCallback onError
    ) {
        return execute("POST", path, data, null, headers, onError, contentType);
    }

    static <T> ResponseModel<T> execute(
            String method,
            String path,
            Object data,
            Map<String, String> query,
            Headers headers,
            ErrorCallback onError,
            Class<T> contentType
    ) {

        ResponseModel<T> responseModel = null;

        try {
            URL url = new URL(API.BASE_URL + path + mapToQuery(query));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            if(headers != null) {
                for (String header: headers.keySet()){
                    conn.setRequestProperty(header, String.join(",", headers.get(header)));
                }
            }
            if (data != null && method.equals("POST")) {
                byte[] bytes = GSON.toJson(data).getBytes();
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                conn.getOutputStream().write(bytes);

            }

            if(conn.getResponseCode() == StatusCode.SUCCESS){
                responseModel = GSON.fromJson(
                        new InputStreamReader(conn.getInputStream()),
                        TypeToken.getParameterized(ResponseModel.class, contentType).getType());
            }
            else {
                responseModel = GSON.fromJson(
                        new InputStreamReader(conn.getErrorStream()),
                        TypeToken.getParameterized(ResponseModel.class, contentType).getType());
            }
            conn.disconnect();


        } catch (Exception e) {
            onError.onError(e);
        }

        return responseModel;
    }

    static <T> ResponseModel<T> execute(
            String method,
            String path,
            Serializable data,
            Map<String, String> query,
            Headers headers,
            ErrorCallback onError,
            Class<T> contentType
    ) {
        ResponseModel<T> responseModel = null;

        try {
            URL url = new URL(API.BASE_URL + path + mapToQuery(query));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if(headers != null) {
                for (String header: headers.keySet()){
                    conn.setRequestProperty(header, String.join(",", headers.get(header)));
                }
            }

            if (data != null && method.equals("POST")) {
                byte[] bytes = SerializationUtils.serialize(data);
                conn.setRequestProperty("Content-Type", "application/octet-stream");
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                conn.getOutputStream().write(bytes);
            } else {
                conn.setRequestProperty("Content-Type", "application/json");
            }

            if(conn.getResponseCode() == StatusCode.SUCCESS){
                responseModel = GSON.fromJson(
                        new InputStreamReader(conn.getInputStream()),
                        TypeToken.getParameterized(ResponseModel.class, contentType).getType());
            }
            else {
                responseModel = GSON.fromJson(
                        new InputStreamReader(conn.getErrorStream()),
                        TypeToken.getParameterized(ResponseModel.class, contentType).getType());
            }
        } catch (Exception e) {
            onError.onError(e);
        }

        return responseModel;
    }


    private static String mapToQuery(Map<String, String> query) {
        StringBuilder queryStr = new StringBuilder();

        if (query != null) {
            queryStr = new StringBuilder("?");
            for (Map.Entry<String, String> entry : query.entrySet()) {
                String entity = entry.getValue();
                try {
                    entity = URLEncoder.encode(entity, StandardCharsets.UTF_8);
                } catch (Exception ignore) {
                }

                queryStr.append(entry.getKey()).append("=").append(entity).append("&");
            }
            queryStr = new StringBuilder(queryStr.substring(0, queryStr.length() - 1));
        }

        return queryStr.toString();
    }
}
