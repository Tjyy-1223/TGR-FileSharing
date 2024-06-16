package com.tjyy.sharing.api.vo.seo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Tjyy
 * @date: 2024-06-14 23:38
 * @description: SeoTag: key - val 两个字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeoTagVo {
    private String key;
    private String val;
}
