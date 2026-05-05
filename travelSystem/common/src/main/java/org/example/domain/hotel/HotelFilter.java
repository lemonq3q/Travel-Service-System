package org.example.domain.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelFilter {

    private String cityCode;        // 城市编码
    private Integer priceMin;       // 价格最小
    private Integer priceMax;       // 价格最大
    private List<Integer> starLevels; // 星级 [3,4,5]
    private Double ratingMin;       // 最低评分

}
