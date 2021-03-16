package com.net.model.chat;

import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;

/**
 * @author Created by zyh on 2020/4/21
 */
public class CommandPhraseModifyInfo {
    public static class Base extends BaseResponse<CommandPhraseModifyInfo> {

    }

    public ArrayList<String> chatPhrases;
}
