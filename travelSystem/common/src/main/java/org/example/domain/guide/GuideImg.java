package org.example.domain.guide;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.ImageBind;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "guide_img")
public class GuideImg extends ImageBind {

    private Integer sortIndex;

}
