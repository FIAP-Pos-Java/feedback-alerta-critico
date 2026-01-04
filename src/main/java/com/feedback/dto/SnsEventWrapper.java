package com.feedback.dto;

import java.util.List;

public class SnsEventWrapper {
    private List<SnsRecord> Records;

    public SnsEventWrapper() {
    }

    public List<SnsRecord> getRecords() {
        return Records;
    }

    public void setRecords(List<SnsRecord> records) {
        Records = records;
    }

    public static class SnsRecord {
        private String EventSource;
        private String EventVersion;
        private String EventSubscriptionArn;
        private SnsMessage Sns;

        public SnsRecord() {
        }

        public String getEventSource() {
            return EventSource;
        }

        public void setEventSource(String eventSource) {
            EventSource = eventSource;
        }

        public String getEventVersion() {
            return EventVersion;
        }

        public void setEventVersion(String eventVersion) {
            EventVersion = eventVersion;
        }

        public String getEventSubscriptionArn() {
            return EventSubscriptionArn;
        }

        public void setEventSubscriptionArn(String eventSubscriptionArn) {
            EventSubscriptionArn = eventSubscriptionArn;
        }

        public SnsMessage getSns() {
            return Sns;
        }

        public void setSns(SnsMessage sns) {
            Sns = sns;
        }
    }

    public static class SnsMessage {
        private String Type;
        private String MessageId;
        private String TopicArn;
        private String Subject;
        private String Message;
        private String Timestamp;

        public SnsMessage() {
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getMessageId() {
            return MessageId;
        }

        public void setMessageId(String messageId) {
            MessageId = messageId;
        }

        public String getTopicArn() {
            return TopicArn;
        }

        public void setTopicArn(String topicArn) {
            TopicArn = topicArn;
        }

        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        public String getTimestamp() {
            return Timestamp;
        }

        public void setTimestamp(String timestamp) {
            Timestamp = timestamp;
        }
    }
}

