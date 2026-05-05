package org.example.domain.scenic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenicSelectDTO {

    private String name;

    private String areaCode;

    private Integer needTicket;

    private Integer pageNum;

    private Integer pageSize;

}
