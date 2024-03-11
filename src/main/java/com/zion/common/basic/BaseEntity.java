package com.zion.common.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Database basic info
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends BasicCondition implements Serializable {

    @Id
    public Long id;

    public LocalDateTime createdTime;

    public String createdUser;

    public LocalDateTime updatedTime;

    public String updatedUser;

    public Integer version;

    public Integer deleted;

}
