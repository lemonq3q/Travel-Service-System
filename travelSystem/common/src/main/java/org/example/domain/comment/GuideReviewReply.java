package org.example.domain.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "guide_review_reply")
public class GuideReviewReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asGuideReview;

    private Long asUser;

    private Long replyUser;

    private String content;

    private Integer likeCount;

    private Integer dislikeCount;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

}
