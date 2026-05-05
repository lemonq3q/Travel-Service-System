package org.example.domain.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSelectDTO {
    private String name;

    private Integer starLevel;

    private String areaCode;

    private Integer pageNum;

    private Integer pageSize;
}
