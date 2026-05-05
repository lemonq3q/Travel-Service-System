package org.example.domain.scenic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.SystemFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "scenic")
public class Scenic {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String areaCode;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String scenicDesc;

    private String openTimeDesc;

    private BigDecimal rating;

    private BigDecimal hotValue;

    private Integer needTicket;

    private BigDecimal price;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

    @TableField(exist = false)
    private List<SystemFile> scenicImgList;

}
