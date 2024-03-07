package com.zion.resource.user.model;

import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
public class User extends BaseEntity  {

    private String loginName;
    private String nickName;
    private String telephone;
    private String avatar;
    private String password;
    private String email;


}
