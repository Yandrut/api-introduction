package org.yandrut.api;

public class SuccessfulRegistration {
    private final Integer id;
    private final String token;

    public SuccessfulRegistration(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
