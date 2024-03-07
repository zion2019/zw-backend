package com.zion.common.vo.resource.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserQO {

    private Long id;

    private String loginName;

    private String password;

    private String nickName;

    private String avatar;

    private String email;

    private String telephone;

}
