package org.example.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "scenic_order")
public class ScenicOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asUser;

    private Long asScenic;

    private BigDecimal payAmount;

    private Integer isPay;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
