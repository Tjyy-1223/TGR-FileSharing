package com.tjyy.sharing.api.vo.seo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: Tjyy
 * @date: 2024-06-14 23:37
 * @description:
 */
@Data
@Builder
public class Seo {
    private List<SeoTagVo> ogp;
    private Map<String, Object> jsonLd;
}
