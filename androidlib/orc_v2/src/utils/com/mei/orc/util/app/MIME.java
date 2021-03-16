package com.mei.orc.util.app;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/20.
 */

public interface MIME {

    String[][] MIME_MapTable = {
            //{后缀名，	MIME类型}
            {".3gp", "video/3gpp"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp4", "video/mp4"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mpga", "audio/mpeg"},
            {".ogg", "audio/ogg"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".bmp", "image/bmp"},
            {".gif", "image/gif"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".png", "image/png"},
            {".c", "text/plain"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".java", "text/plain"},
            {".log", "text/plain"},
            {".prop", "text/plain"},
            {".sh", "text/plain"},
            {".txt", "text/plain"},
            {".xml", "text/plain"},
            {".rc", "text/plain"},
            {".bin", "application/octet-stream"},
            {".class", "application/octet-stream"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".jar", "application/java-archive"},
            {".js", "application/x-javascript"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".msg", "application/vnd.ms-outlook"},
            {".pdf", "application/pdf"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".rtf", "application/rtf"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".wps", "application/vnd.ms-works"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    String[] VIDEO_MIME = {
            "video/3gpp",
            "video/x-ms-asf",
            "video/x-msvideo",
            "video/vnd.mpegurl",
            "video/x-m4v",
            "video/quicktime",
            "video/mp4",
            "video/mpeg",
            "video/mpeg",
            "video/mpeg",
            "video/mp4"
    };

    String[] AUDIO_MIME = {
            "audio/x-wav",
            "audio/x-ms-wma",
            "audio/x-ms-wmv",
            "audio/x-mpegurl",
            "audio/mp4a-latm",
            "audio/mp4a-latm",
            "audio/mp4a-latm",
            "audio/x-mpeg",
            "audio/x-mpeg",
            "audio/mpeg",
            "audio/ogg",
            "audio/x-pn-realaudio"
    };

    String[] IMAGE_MIME = {
            "image/bmp",
            "image/gif",
            "image/jpeg",
            "image/jpeg",
            "image/png"
    };
}
