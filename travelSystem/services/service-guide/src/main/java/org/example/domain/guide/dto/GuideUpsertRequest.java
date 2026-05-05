package org.example.domain.guide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideUpsertRequest {

    private String title;

    private String content;

    private List<GuideImageDTO> images;

    private List<Long> imageFileIds;
}

