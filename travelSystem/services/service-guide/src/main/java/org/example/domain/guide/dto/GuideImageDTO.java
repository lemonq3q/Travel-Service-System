package org.example.domain.guide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideImageDTO {

    private Long fileId;

    private String url;

    private Integer sortIndex;
}

