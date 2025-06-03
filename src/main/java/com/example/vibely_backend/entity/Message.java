package com.example.vibely_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import java.util.Date;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    @Field("conversation_id")
    private String conversationId;

    @Field("sender_id")
    private String senderId;

    @Field("content")
    private String content;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("is_read")
    private boolean isRead;

    public Message() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isRead = false;
    }
}
