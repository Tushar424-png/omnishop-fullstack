package com.Ecommerce.dto;

public class GeminiResponse {
    private String reply;

    public GeminiResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
