package com.joker.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/8.
 */

public class TestAliasActivity extends Activity {
    /**
     * This is the name under which you should store in your component the
     * meta-data information about the alias.  It is a reference to an XML
     * resource describing an intent that launches the real application.
     * {@hide}
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            startActivity(new Intent(this, Class.forName(getString(R.string.test_launcher))));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
