package com.net.model.chat;

import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;

/**
 * @author Created by zyh on 2020/4/21
 */
public class CommandPhrase {
    public ArrayList<String> chatPhrases;

    public static class Base extends BaseResponse<CommandPhrase> {
    }

}
