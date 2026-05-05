package org.example.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "aircraft_order")
public class AircraftOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asUser;

    private String asAircraft;

    private String startStationName;

    private String endStationName;

    private String typeName;

    private BigDecimal payAmount;

    private Integer isPay;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;
}

