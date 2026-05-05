package org.example.domain.comment;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hotel_review_img")
public class HotelReviewImg {

    private Long hotelReviewId;

    private Long fileId;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
