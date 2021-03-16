package com.mei.wood.util.db;

import com.mei.orc.john.model.JohnUser;
import com.mei.orc.util.string.StringUtilKt;

/**
 * Created by joker on 15/12/31.
 */
public class Elite_save_data {

    public Elite_save_data() {
    }

    public Elite_save_data(long id, long time) {
        this.id = StringUtilKt.stringConcate(id, JohnUser.getSharedUser().getUserID());
        this.time = time;
    }

    public String id;
    public long time;
}
