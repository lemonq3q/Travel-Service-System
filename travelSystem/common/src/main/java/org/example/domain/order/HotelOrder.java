package org.example.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hotel_order")
public class HotelOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asUser;

    private Long asHotel;

    private Long asRoom;

    private Integer roomNum;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkOutDate;

    private BigDecimal payAmount;

    private Integer isPay;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
