package com.mei.widget.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import org.jetbrains.annotations.NotNull;

/**
 * @author Created by lenna on 2020/7/20
 */
public class InputWebView extends InitWebView {
    public InputWebView(@NotNull Context context) {
        super(context);
    }

    public InputWebView(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    public InputWebView(@NotNull Context context, @NotNull AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions = outAttrs.imeOptions & ~EditorInfo.IME_FLAG_NAVIGATE_NEXT &
                ~EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS;
        return inputConnection;
    }
}
