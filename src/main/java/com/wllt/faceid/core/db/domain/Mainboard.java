package com.wllt.faceid.core.db.domain;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName mainboard
 */
@Data
public class Mainboard implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 主板id
     */
    private String mainboard;

    private static final long serialVersionUID = 1L;
}
