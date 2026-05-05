package org.example.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSelectDTO {
    private String blurParam;

    private Long roleId;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}

