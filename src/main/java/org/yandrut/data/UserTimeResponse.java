package org.yandrut.data;

import lombok.Getter;

@Getter
public class UserTimeResponse extends UserTime {
    private final String updatedAt;

    public UserTimeResponse (String name, String job, String updatedAt) {
        super(name,job);
        this.updatedAt = updatedAt;
    }
}