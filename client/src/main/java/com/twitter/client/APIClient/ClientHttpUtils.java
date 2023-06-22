package com.twitter.client.APIClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.twitter.client.APIClient.Callbacks.RequestErrorCallback;
import com.twitter.client.APIClient.Callbacks.RequestSuccessCallback;
import com.twitter.common.API.API;
import com.twitter.common.API.ResponseModel;
import com.twitter.common.API.StatusCode;
import com.twitter.common.Utils.GsonUtils;


import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHttpUtils {
    private final static Gson GSON = GsonUtils.getInstance();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static <T> void get(
            String path,
            Map<String, String> query,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
       execute("GET", path, null, query, null, contentType, onError, onSuccess);
    }

    public static <T> void get(
            String path,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute("GET", path, null, query, headers, contentType, onError, onSuccess);
    }

    public static <T> void post(
            String path,
            Object data,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute("POST", path, data, null, null ,contentType, onError, onSuccess);
    }

    public static <T> void post(
            String path,
            Object data,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute("POST", path, data, null, headers ,contentType, onError, onSuccess);
    }

    public static <T> void postSerialized(
            String path,
            Serializable data,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        executeSerializable("POST", path, data, null, headers, contentType, onError, onSuccess);
    }

    private static <T> void execute(
            String method,
            String path,
            Object data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        executorService.submit(()-> {
            try {
                ResponseModel<T> responseModel = executeRequest(method, path, data, query, headers, contentType);
                if(responseModel != null) {
                    onSuccess.onRequestSuccess(responseModel);

                } else {
                    onError.onRequestError(new Exception("Request failed"));
                }
            } catch (Exception e) {
                onError.onRequestError(e);
            }
        });
    }

    private static <T> void executeSerializable(
            String method,
            String path,
            Serializable data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        executorService.submit(()-> {
            try {
                ResponseModel<T> responseModel = executeRequest(method, path, data, query, headers, contentType);
                if (responseModel != null) {
                    onSuccess.onRequestSuccess(responseModel);

                } else {
                    onError.onRequestError(new Exception("Request failed"));
                }

            } catch (Exception e) {
                onError.onRequestError(e);
            }
        });
    }


    private static <T> ResponseModel<T> executeRequest(
            String method,
            String path,
            Object data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType
    ) throws IOException {
        ResponseModel<T> responseModel;

        URL url = new URL(API.BASE_URL + path + mapToQuery(query));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (String header : headers.keySet()) {
                conn.setRequestProperty(header, String.join(",", headers.get(header)));
            }
        }
        if (data != null && method.equals("POST")) {
            conn.setDoOutput(true);
            byte[] bytes = GSON.toJson(data).getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            conn.getOutputStream().write(bytes);

        }

        if (conn.getResponseCode() == StatusCode.SUCCESS) {
            responseModel = GSON.fromJson(
                    new InputStreamReader(conn.getInputStream()),
                    TypeToken.getParameterized(ResponseModel.class, contentType).getType());
        } else {
            responseModel = GSON.fromJson(
                    new InputStreamReader(conn.getErrorStream()),
                    TypeToken.getParameterized(ResponseModel.class, contentType).getType());
        }
        conn.disconnect();
        return responseModel;
    }

    private static <T> ResponseModel<T> executeRequest(
            String method,
            String path,
            Serializable data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType
    ) throws IOException {

        ResponseModel<T> responseModel = null;

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

        return responseModel;
    }

    private static String mapToQuery(Map<String, String> query) {
        StringBuilder queryStr = new StringBuilder();

        if (query != null) {
            queryStr = new StringBuilder("?");
            for (Map.Entry<String, String> entry : query.entrySet()) {
                String entity = entry.getValue();
                try {
                    entity = URLEncoder.encode(entity, StandardCharsets.UTF_8.toString());
                } catch (Exception ignore) {
                }

                queryStr.append(entry.getKey()).append("=").append(entity).append("&");
            }
            queryStr = new StringBuilder(queryStr.substring(0, queryStr.length() - 1));
        }

        return queryStr.toString();
    }
}
