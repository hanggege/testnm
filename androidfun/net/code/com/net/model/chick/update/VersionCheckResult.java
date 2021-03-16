package com.net.model.chick.update;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class VersionCheckResult {

    public static class Response extends BaseResponse<VersionCheckResult> {

    }

    public String path;
    public String updateVersion;
    public String updateMessage;
    public int delayRemindDay;
    public boolean force;
}
