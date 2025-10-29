package com.crm.vo;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-29 16:51
 **/
@Data
public class FollowVO {
    private Integer id;

    private String content;
    private Integer followType;
    private LocalDateTime nextFollowType;
}
