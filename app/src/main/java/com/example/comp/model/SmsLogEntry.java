package com.example.comp.model;

import java.io.Serializable;

/** One sent or received SMS, saved for the Message Log screen. */
public class SmsLogEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean incoming;
    private final String number;
    private final String body;
    private final long timestamp;

    public SmsLogEntry(boolean incoming, String number, String body, long timestamp) {
        this.incoming = incoming;
        this.number = number;
        this.body = body;
        this.timestamp = timestamp;
    }

    public boolean isIncoming() { return incoming; }
    public String getNumber() { return number; }
    public String getBody() { return body; }
    public long getTimestamp() { return timestamp; }
}
