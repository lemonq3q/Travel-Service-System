package org.example.domain.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "scenic_review")
public class ScenicReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asScenic;

    private Long asUser;

    private String content;

    private BigDecimal rating;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;
}
