package org.example.domain.hotel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "room_stock")
public class RoomStock {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate stockDate;

    private Integer occupiedNum;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;
}
