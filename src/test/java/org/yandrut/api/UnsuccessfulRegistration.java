package org.yandrut.api;

public class UnsuccessfulRegistration {
    private final String error;

    public UnsuccessfulRegistration(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
