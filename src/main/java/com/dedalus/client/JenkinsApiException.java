package com.dedalus.client;

public class JenkinsApiException extends Exception {

    public JenkinsApiException(String message) {
        super(message);
    }

    public JenkinsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
