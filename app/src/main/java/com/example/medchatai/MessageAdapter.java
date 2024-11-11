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

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends ArrayAdapter<Message> {
    private static final int TYPE_USER = 0;
    private static final int TYPE_ROBOT = 1;

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
            imageView.setImageBitmap(message.getImage());
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else if (message.getType() == Message.TYPE_COMPOSITE) {
            if (message.getContent() != null) {
                textView.setText(message.getContent());
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }

            if (message.getImages() != null && !message.getImages().isEmpty()) {
                imageView.setImageBitmap(message.getImages().get(0)); // 显示第一个图片
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}