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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHttpUtils {
    private final static Gson GSON = GsonUtils.getInstance();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public enum DataFormat {Json, Serialized}

    public enum HttpMethod {GET, POST}

    public static <T> void get(
            String path,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute(
                HttpMethod.GET,
                path,
            null,
                query,
                headers,
                contentType,
                DataFormat.Json,
                DataFormat.Json,
                onError,
                onSuccess);
    }

    public static <T> void getSerialized(
            String path,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute(
                HttpMethod.GET,
                path,
                null,
                query,
                headers,
                contentType,
                DataFormat.Json,
                DataFormat.Serialized,
                onError,
                onSuccess);
    }

    public static <T> void post(
            String path,
            Object data,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute(
                HttpMethod.POST,
                path,
                data,
          null,
                headers ,
                contentType,
                DataFormat.Json,
                DataFormat.Json,
                onError,
                onSuccess);
    }

    public static <T> void postSerialized(
            String path,
            Serializable data,
            Headers headers,
            Class<T> contentType,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        execute(
                HttpMethod.POST,
                path,
                data,
                null,
                headers ,
                contentType,
                DataFormat.Serialized,
                DataFormat.Json,
                onError,
                onSuccess);
    }

    private static <T> void execute(
            HttpMethod method,
            String path,
            Object data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            DataFormat requestDataFormat,
            DataFormat responseDataFormat,
            RequestErrorCallback onError,
            RequestSuccessCallback<T> onSuccess
    ) {
        executorService.execute(()-> {
            try {
                ResponseModel<T> responseModel =
                    executeRequest(
                        method,
                        path,
                        data,
                        query,
                        headers,
                        contentType,
                        requestDataFormat,
                        responseDataFormat);

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

    private static <T> ResponseModel<T> executeRequest(
            HttpMethod method,
            String path,
            Object data,
            Map<String, String> query,
            Headers headers,
            Class<T> contentType,
            DataFormat requestDataFormat,
            DataFormat responseDataFormat
    ) throws IOException {

        ResponseModel<T> responseModel = null;
        HttpURLConnection conn = prepareConnection(path, query, method);
        setHeaders(conn, headers);

        String setContentType = requestDataFormat == DataFormat.Json ? "application/json" : "application/octet-stream";
        conn.setRequestProperty("Content-Type", setContentType);


        if (data != null && method == HttpMethod.POST) {
            byte[] requestData;

            if (requestDataFormat == DataFormat.Json) {
                requestData = GSON.toJson(data).getBytes();

            } else {
                requestData = SerializationUtils.serialize((Serializable) data);
            }
            doRequest(conn, requestData);
        }

        boolean isError = conn.getResponseCode() != StatusCode.SUCCESS;
        if(responseDataFormat == DataFormat.Json) {
            responseModel = readJsonResponse(conn, contentType, isError);

        } else if (responseDataFormat == DataFormat.Serialized) {
            responseModel = readSerializedResponse(conn, isError);
        }

        conn.disconnect();
        return responseModel;
    }

    private static HttpURLConnection prepareConnection(
            String path,
            Map<String,
            String> query,
            HttpMethod method
    ) throws  IOException {

            URL url = new URL(API.BASE_URL + path + mapToQuery(query));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.name());

            return conn;
    }

    private static void setHeaders(HttpURLConnection connection, Headers headers) {
        if(headers != null) {
            for (String header: headers.keySet()){
                connection.setRequestProperty(header, String.join(",", headers.get(header)));
            }
        }
    }

    private static void doRequest(HttpURLConnection connection, byte[] bytes) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        connection.getOutputStream().write(bytes);
    }

    private static <T> ResponseModel<T> readJsonResponse(HttpURLConnection conn, Class<T> contentType, boolean isError) throws IOException {
        InputStream inputStream = isError ? conn.getErrorStream() : conn.getInputStream();
        return GSON.fromJson(
                new InputStreamReader(inputStream),
                TypeToken.getParameterized(ResponseModel.class, contentType).getType());
    }

    private static <T> ResponseModel<T> readSerializedResponse(HttpURLConnection conn, boolean isError) throws IOException {
        ResponseModel<T> responseModel;
        try(InputStream inputStream = isError ? conn.getErrorStream() : conn.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            Object deserializedObject = SerializationUtils.deserialize(bytes);
            responseModel = (ResponseModel<T>) deserializedObject;
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
