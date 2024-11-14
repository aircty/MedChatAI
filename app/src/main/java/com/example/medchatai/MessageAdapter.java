package com.example.medchatai;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends ArrayAdapter<Message> {
    private static final int TYPE_USER = 0;
    private static final int cornerRadius = 20;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = Objects.requireNonNull(getItem(position));
        return message.getSenderType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Message message = Objects.requireNonNull(getItem(position));
        int viewType = getItemViewType(position);

        if (convertView == null) {
            int layoutId = viewType == TYPE_USER ? R.layout.message_item_user : R.layout.message_item_robot;
            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.iv_image);
        TextView textView = convertView.findViewById(R.id.tv_text);

        // 根据消息类型显示内容
        if (message.getType() == Message.TYPE_TEXT) {
            textView.setText(message.getContent());
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else if (message.getType() == Message.TYPE_IMAGE) {
            Bitmap imageBitmap = message.getImage();
            if (imageBitmap != null) {
                setRoundedBitmapDrawable(imageBitmap, imageView);
                setSquare(imageView);
            }
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else if (message.getType() == Message.TYPE_COMPOSITE) {
            // 显示图片
            if (!message.getImages().isEmpty()) {
                Bitmap imageBitmap = message.getImages().get(0);
                setRoundedBitmapDrawable(imageBitmap, imageView);
                setSquare(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }

            // 显示文字
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                textView.setText(message.getContent());
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void setSquare(ImageView imageView) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = 100; // 设置宽度
        layoutParams.height = 100; // 设置高度
        imageView.setLayoutParams(layoutParams);
    }

    private void setRoundedBitmapDrawable(Bitmap imageBitmap, ImageView imageView) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), imageBitmap);
        roundedBitmapDrawable.setCornerRadius(cornerRadius); // 设置圆角半径，单位是像素
        imageView.setImageDrawable(roundedBitmapDrawable);
    }
}