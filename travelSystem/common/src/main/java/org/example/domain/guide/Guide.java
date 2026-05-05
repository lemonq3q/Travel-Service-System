package org.example.domain.guide;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.SystemFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "guide")
public class Guide {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asUser;

    private String title;

    private String content;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

    @TableField(exist = false)
    private List<SystemFile> imgList;

}