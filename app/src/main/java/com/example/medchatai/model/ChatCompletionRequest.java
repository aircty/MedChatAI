package com.example.medchatai.model;

import androidx.annotation.NonNull;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class ChatCompletionRequest {
    public long frequencyPenalty;
    public Object logitBias;
    public ChatMessage[] messages;
    public String model;
    public long n;
    public long presencePenalty;
    public Object stop;
    public boolean stream;
    public long temperature;
    public long topP;
    public Object user;

    public ChatCompletionRequest(String model, ChatMessage[] messages) {
        this.model = model;
        this.messages = messages;
    }


    @NonNull
    @Override
    public String toString() {
        JsonArrayBuilder messagesArrayBuilder = Json.createArrayBuilder();
        if (messages != null) {
            for (ChatMessage message : messages) {
                JsonObjectBuilder messageBuilder = Json.createObjectBuilder()
                        .add("content", message.content)
                        .add("role", message.role);
                messagesArrayBuilder.add(messageBuilder.build());
            }
        }

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("frequencyPenalty", frequencyPenalty)
                .add("logitBias", toJsonValue(logitBias))
                .add("messages", messagesArrayBuilder.build())
                .add("model", model)
                .add("n", n)
                .add("presencePenalty", presencePenalty)
                .add("stop", toJsonValue(stop))
                .add("stream", stream)
                .add("temperature", temperature)
                .add("topP", topP)
                .add("user", toJsonValue(user));

        return builder.build().toString();
    }

    private JsonValue toJsonValue(Object value) {
        if (value instanceof Number) {
            return Json.createValue((Integer) value);
        } else if (value instanceof Boolean) {
            return Json.createValue(String.valueOf((Boolean) value));
        } else if (value instanceof String) {
            return Json.createValue((String) value);
        } else if (value == null) {
            return JsonValue.NULL;
        } else {
            // 如果值不是基本类型，可以考虑将其转换为字符串或其他处理方式
            return Json.createValue(value.toString());
        }
    }
}

class ChatMessage {
    public String content;
    public String role;
}