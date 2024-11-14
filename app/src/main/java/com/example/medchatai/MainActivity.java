package com.example.medchatai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.medchatai.model.ChatCompletion;
import com.example.medchatai.model.ChatCompletionRequest;
import com.example.medchatai.model.UserInputModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int REQUEST_CODE_TAKE_PICTURE = 3;
    private List<Message> messages;
    private MessageAdapter adapter;
    private DrawerLayout drawerLayout;
    private EditText editText;
    private ListView conversationList;
    private Context context;
    private UserInputModel userInputModel;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取根布局
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.WHITE);

        findViewById(R.id.btn_search).setOnClickListener(view -> handleUserInput());
        ListView listView = findViewById(R.id.conversationList);
        drawerLayout = findViewById(R.id.drawer_layout);
        editText = findViewById(R.id.et_Input);
        conversationList = findViewById(R.id.conversationList);
        userInputModel = new UserInputModel();


        // 点击相册访问权限
        findViewById(R.id.iv_pdf).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS
                );
            } else
                selectImage();
        });

        // 点击访问摄像头
        findViewById(R.id.iv_camera).setOnClickListener(v -> {
                    if (ActivityCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.CAMERA
                        }, 1);
                    } else
                        camera();

                }
        );

        // 打开/关闭抽屉
        findViewById(R.id.bl_more_vert_24).setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        conversationList.setAdapter(adapter);
        editText.setFilters(new InputFilter[]{new ImageSpanInputFilter(1, editText, context)});
    }

    // 列出用户所输入的文本

    private void handleUserInput() {
        Spannable textWithImage = editText.getText();

        if (textWithImage != null) {
            Log.d("MainActivity", "Handling user input with Spannable text");

            ImageSpan[] spans = textWithImage.getSpans(0, textWithImage.length(), ImageSpan.class);

            if (spans.length > 0) {
                Log.d("MainActivity", "Found ImageSpans in the input");

                // 组合所有文本和图片成一个复合消息
                StringBuilder combinedText = new StringBuilder();
                List<Bitmap> combinedImages = new ArrayList<>();

                for (ImageSpan span : spans) {
                    int start = textWithImage.getSpanStart(span);
                    int end = textWithImage.getSpanEnd(span);
                    Bitmap bitmap = ((BitmapDrawable) span.getDrawable()).getBitmap();
                    String spanText = textWithImage.subSequence(start, end).toString();
                    combinedText.append(spanText);
                    combinedImages.add(bitmap);
                }

                // 添加剩余的纯文本
                String remainingText = textWithImage.toString().trim();
                userInputModel.setText(combinedText.toString());
                combinedText.append(remainingText);

                // 创建复合消息
                messages.add(new Message(userInputModel.getText(), combinedImages, Message.TYPE_COMPOSITE, Message.TYPE_USER));
                Log.d("MainActivity", "Added composite message with text: " + combinedText.toString());
            } else {
                Log.d("MainActivity", "No ImageSpans found in the input");

                // 将纯文本消息添加到消息列表
                String text = textWithImage.toString().trim();
                userInputModel.setText(text);
                if (!text.isEmpty()) {
                    messages.add(new Message(userInputModel.getText(), Message.TYPE_TEXT, Message.TYPE_USER));
                    Log.d("MainActivity", "Added text message: " + text);
                }

            }
        } else {
            Log.d("MainActivity", "Handling user input without Spannable text");

            // 将纯文本消息添加到消息列表
            String text = editText.getText().toString().trim();
            userInputModel.setText(text);
            if (!text.isEmpty()) {
                messages.add(new Message(userInputModel.getText(), Message.TYPE_TEXT, Message.TYPE_USER));
                Log.d("MainActivity", "Added text message: " + text);
            }
        }

        // 模拟机器人的回复
        simulateBotResponse(userInputModel);

        // 清空 EditText
        editText.setText("");
        Log.d("MainActivity", "Cleared EditText");

        // 滚动到底部显示最新消息
        conversationList.smoothScrollToPosition(adapter.getCount() - 1);
        Log.d("MainActivity", "Scrolled to the bottom of the list");
    }


    // 模拟AI回复
    private void simulateBotResponse(UserInputModel userInputModel) {
        Log.d("MainActivity", "用户输入:" + userInputModel.getText());
//
//        Log.d("MainActivity", "AI的回答： " + chatCompletion.getText());
//        messages.add(new Message(chatCompletion.getText(), Message.TYPE_TEXT, Message.TYPE_BOT));
        adapter.notifyDataSetChanged();
        // 滚动到底部显示最新消息
        conversationList.smoothScrollToPosition(adapter.getCount() - 1);// 模拟1秒的延迟
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void camera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0) {
            selectImage();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectImageUri = data.getData();
            if (selectImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());

                    SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    ssb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Editable editable = editText.getText();
                    int start = editText.getSelectionStart();
                    editable.insert(start, ssb);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                assert imageBitmap != null;
                drawable.setBounds(0, 0, imageBitmap.getWidth(), imageBitmap.getHeight());

                SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                ssb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                Editable editable = editText.getText();
                int start = editText.getSelectionStart();
                editable.insert(start, ssb);
            }
        }
    }


}