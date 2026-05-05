package org.example.domain.scenic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.ImageBind;

@NoArgsConstructor
@TableName(value = "scenic_img")
public class ScenicImg extends ImageBind {

}
