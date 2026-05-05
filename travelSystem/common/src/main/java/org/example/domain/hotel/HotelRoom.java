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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hotel_room")
public class HotelRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long asHotel;

    private String name;

    private String roomDesc;

    private String featureJson;

    private String tagJson;

    private Integer bedCount;

    private BigDecimal bedSize;

    private Integer maxPeople;

    private Integer totalRoom;

    private Long createTime;

    private Long updateTime;

    private Long updateBy;

    private Integer isDelete;

    @TableField(exist = false)
    private HotelRoom cheapestRoom;

    @TableField(exist = false)
    private List<SystemFile> roomImgList;

    @TableField(exist = false)
    private List<RoomTypePrice> roomTypePriceList;

}
