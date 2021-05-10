package com.fireflyest.fiot.bean;

public class Command {

    private long id;

    private String device;

    private boolean sending;

    private boolean success;

    private long time;

    private String text;

    private int type;

    public Command() {
    }

    public Command(long id, String device, boolean sending, boolean success, long time, String text, int type) {
        this.id = id;
        this.device = device;
        this.sending = sending;
        this.success = success;
        this.time = time;
        this.text = text;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", device='" + device + '\'' +
                ", sending=" + sending +
                ", success=" + success +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", type=" + type +
                '}';
    }
}
