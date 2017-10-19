package com.baofeng.ad.mobiletest.notify;

public class NotifyData {
    private String title;
    private String content;

    public NotifyData() {
        this.title = "";
        this.content = "";
    }

    public NotifyData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title.trim();
    }

    public String getContent() {
        return content.trim();
    }

    public void addTitle(String title) {
        this.title += title + "; ";
    }

    public void addContent(String content) {
        this.content += content + "\r\n\r\n" ;
    }

    public boolean isEmpty() {
        return title.isEmpty() && content.isEmpty();
    }
}
