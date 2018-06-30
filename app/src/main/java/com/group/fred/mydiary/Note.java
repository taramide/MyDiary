package com.group.fred.mydiary;

public class Note {
    String header;
    String subject;
    String created;
    String last_updated;

    public Note() {
    }

    public Note(String header, String subject, String created, String last_updated) {
        this.header = header;
        this.subject = subject;
        this.created = created;
        this.last_updated = last_updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
