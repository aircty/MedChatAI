package com.example.medchatai;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.Toast;

public class ImageSpanInputFilter implements InputFilter {

    private final int maxImageCount;
    private final Context context;

    public ImageSpanInputFilter(int maxImageCount, EditText editText, Context context) {
        this.maxImageCount = maxImageCount;
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(dest);
        ssb.replace(dstart, dend, source);

        int imageSpanCount = ssb.getSpans(0, ssb.length(), ImageSpan.class).length;

        if (imageSpanCount > maxImageCount) {
            Toast.makeText(context, "只能插入一张图片", Toast.LENGTH_SHORT).show();
            return "";
        }

        return null;
    }
}