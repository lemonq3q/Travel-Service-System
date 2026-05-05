package org.example.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBind {

    private Long asId;

    private Long fileId;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
