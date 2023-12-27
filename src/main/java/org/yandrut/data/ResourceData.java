package org.yandrut.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResourceData {
    private Integer id;
    private String name;
    private Integer year;
    private String color;
    private String pantone_value;
}