package org.example.domain.comment;

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
@TableName(value = "hotel_review")
public class HotelReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asHotel;

    private Long asUser;

    private Long asRoom;

    private String content;

    private BigDecimal rating;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
