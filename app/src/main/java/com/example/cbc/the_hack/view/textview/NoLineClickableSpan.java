package com.example.cbc.the_hack.view.textview;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.example.cbc.R;


public class NoLineClickableSpan extends ClickableSpan {

    private Context mContext;

    public NoLineClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View widget) {
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        // 文本颜色
        ds.setColor(mContext.getResources().getColor(R.color.blue));
        // 去掉超链接下划线
        ds.setUnderlineText(false);
    }
}