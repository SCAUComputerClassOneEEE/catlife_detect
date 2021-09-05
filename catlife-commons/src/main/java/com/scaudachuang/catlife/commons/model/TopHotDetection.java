package com.scaudachuang.catlife.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopHotDetection {
    public static final String redisZSetKey = "top_hot_detection";

    private String catClass;
}
