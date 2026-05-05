package org.example.domain.hotel;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import org.example.domain.ImageBind;

@NoArgsConstructor
@TableName(value = "room_img")
public class RoomImg extends ImageBind {
}
