package com.feedback.dto;

public class SnsEvent {
    private String Type;
    private String MessageId;
    private String TopicArn;
    private String Subject;
    private String Message;
    private String Timestamp;

    public SnsEvent() {
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        this.MessageId = messageId;
    }

    public String getTopicArn() {
        return TopicArn;
    }

    public void setTopicArn(String topicArn) {
        this.TopicArn = topicArn;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.Timestamp = timestamp;
    }
}

