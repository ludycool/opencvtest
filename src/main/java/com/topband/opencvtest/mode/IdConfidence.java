package com.topband.opencvtest.mode;

import lombok.Data;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/28 17:23
 * @remark id值 与可信度
 */
@Data
public class IdConfidence {

    /**
     * 标签
     */
    private  int label;
    /**
     * id
     */
    private  String Id;
    /**
     * 可信度
     */
    private  double confidence;
}
