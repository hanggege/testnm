package com.mei.orc.http.log;

import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by guoyufeng on 7/9/16.
 */
public class BetterLogger implements HttpLoggingInterceptor.Logger {

    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private static final String http_request_regex = "--> (.*) (http[s]?:.*)";
    private static final Pattern http_request_pattern = Pattern.compile(http_request_regex);
    private int indent_space = 4;

    public static final boolean log_json_include_raw = false;

    private static BetterLogger INSTANCE;

    public static BetterLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BetterLogger();
        }
        return INSTANCE;
    }

    private BetterLogger() {
        //hide constructor to prevent break single instance;
    }

    @Override
    public void log(String message) {
        if (message.matches("\\{.*\\}")) {
            if (log_json(message)) {
                return;
            }
        } else if (message.matches(http_request_regex)) {
            log_request(message);
            return;
        }
        Log.d("retrofit", message);
    }

    private synchronized void log_request(String message) {
//        message.matches("---> HTTP (.*) http:.*")
        String tag = "BL_request";
        Matcher matcher = http_request_pattern.matcher(message);
        if (matcher.matches()) {
            String method = matcher.group(1);
            String url = matcher.group(2);
            log_top_divider(tag);
            //log url
            log_content(tag, method + " " + url);
            //log params
            Map<String, String> query_params = get_query_params(url);
            if (query_params != null && query_params.size() > 0) {
                log_middle_divider(tag);
                int max_key_length = get_max_length(query_params.keySet());
                String format = "%1$-" + max_key_length + "s = %2$s";
                for (Map.Entry<String, String> entry : query_params.entrySet()) {
                    log_content(tag, String.format(format, entry.getKey(), entry.getValue()));
                }
            }

            log_bottom_divider(tag);
        }
    }

    private int get_max_length(Set<String> query_params) {
        int max = 0;
        for (String str : query_params) {
            int length = str.length();
            if (length > max) {
                max = length;
            }
        }
        return max;
    }

    public static Map<String, String> get_query_params(String url) {
        try {
            Map<String, String> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    String old_value = params.get(key);
                    if (old_value == null) {
                        params.put(key, value);
                    } else {
                        params.put(key, old_value + "," + value);
                    }
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    private synchronized boolean log_json(String message) {
        String tag = "BL_json";
        try {
            String pretty_json = new JSONObject(message).toString(indent_space);
            String[] json_for_each_row = pretty_json.split("\n");
            if (json_for_each_row != null && json_for_each_row.length > 0) {
                log_top_divider(tag);

                for (String s : json_for_each_row) {
                    log_content(tag, s.replace("\\", ""));
                }
                if (log_json_include_raw) {
                    log_middle_divider(tag);
                    log_content(tag, message);
                }
                log_bottom_divider(tag);
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    private void log_middle_divider(String tag) {
        Log.d(tag, MIDDLE_BORDER);
    }

    private void log_content(String tag, String content) {
        Log.d(tag, HORIZONTAL_DOUBLE_LINE + " " + content);
    }

    private void log_bottom_divider(String tag) {
        Log.d(tag, BOTTOM_BORDER);
    }

    private void log_top_divider(String tag) {
        Log.d(tag, TOP_BORDER);
    }
}
