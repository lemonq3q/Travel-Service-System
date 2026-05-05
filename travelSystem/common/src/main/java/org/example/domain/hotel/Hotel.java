package org.example.domain.hotel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.SystemFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hotel")
public class Hotel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String areaCode;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String hotelDesc;

    private String featureJson;

    private String facilityJson;

    private String serviceJson;

    private BigDecimal rating;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private Integer starLevel;

    private Long coverImg;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

    @TableField(exist = false)
    private List<HotelRoom> hotelRoomList;

    @TableField(exist = false)
    private List<SystemFile> hotelImgList;


}
