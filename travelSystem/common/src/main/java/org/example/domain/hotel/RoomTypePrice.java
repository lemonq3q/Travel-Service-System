package org.example.domain.hotel;

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
@TableName(value = "room_type_price")
public class RoomTypePrice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asRoom;

    private String summaryJson;

    private BigDecimal price;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;
}
