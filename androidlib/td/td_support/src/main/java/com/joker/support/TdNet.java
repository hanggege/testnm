package com.joker.support;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steven on 1/19/16.
 */
public class TdNet {

    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int READ_TIMEOUT = 15 * 1000;


    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String queryString(Map<Object, Object> params) {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map) {
                for (Map.Entry<Object, Object> subEntry : ((Map<Object, Object>) value).entrySet()) {
                    String subKey = subEntry.getKey().toString();
                    Object subValue = subEntry.getValue();
                    pairs.add(String.format("%s[%s]=%s", key, subKey, urlEncode(subValue.toString())));
                }
            } else if (value instanceof List) {
                for (Object subValue : (List) value) {
                    pairs.add(String.format("%s[]=%s", key, urlEncode(subValue.toString())));
                }
            } else {
                pairs.add(String.format("%s=%s", key, urlEncode(value.toString())));
            }
        }
        return TextUtils.join("&", pairs);
    }

    public static void writeString(String string, OutputStream os) {
        try {
            byte[] outputInBytes = string.getBytes("UTF-8");
            os.write(outputInBytes);
            os.close();
        } catch (Exception e) {

        }
    }

    public static String readString(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {

        }
        return "";
    }

    public static HttpURLConnection urlConnection(String url, String method) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(TextUtils.equals("POST", method));
        } catch (Exception e) {

        }
        return conn;
    }

    private static boolean handleResponseCode(int statusCode) throws Exception {
        if (statusCode != 200) {
            throw new IllegalArgumentException("invalid status code: " + statusCode);
//            return false;
        }
        return true;
    }

    public static String getStr(String url) throws Exception {
        return readString(get(url, null));
    }

    public static InputStream getStream(String url) throws Exception {
        return get(url, null);
    }

    public static InputStream get(String url, Map<Object, Object> params) throws Exception {
        try {
            String pString = params == null ? "" : queryString(params);
            if (!TextUtils.isEmpty(pString)) {
                url += "?" + pString;
            }
            HttpURLConnection conn = urlConnection(url, "GET");
            handleResponseCode(conn.getResponseCode());
            return conn.getInputStream();

        } catch (Exception e) {
            throw e;
        }
    }

    public static String post(String url, Map<Object, Object> params) throws Exception {
        String body = queryString(params);
        return post(url, body);
    }

    public static String post(String url, String body) throws Exception {
        try {
            HttpURLConnection conn = urlConnection(url, "POST");
            writeString(body, conn.getOutputStream());
            handleResponseCode(conn.getResponseCode());
            return readString(conn.getInputStream());

        } catch (Exception e) {
            throw e;
        }
    }
}
