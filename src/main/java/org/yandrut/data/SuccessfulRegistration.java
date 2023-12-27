package org.yandrut.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SuccessfulRegistration {
    private Integer id;
    private String token;
}