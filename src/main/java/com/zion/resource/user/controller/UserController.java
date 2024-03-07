package com.zion.resource.user.controller;

import com.zion.common.basic.R;
import com.zion.common.utils.SpringSecurityUtil;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.resource.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/res/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    JwtEncoder encoder;

    @PostMapping("/token")
    public R token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;

        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        UserVO userVO = userService.conditionOne(UserQO.builder().loginName(authentication.getName()).build());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        // @formatter:on
        userVO.setToken(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
        return R.ok(userVO);
    }

    @PostMapping("/register")
    public R register(@RequestBody UserQO qo){
        return R.ok( userService.register(qo));
    }


    @GetMapping
    public R info(){
        return R.ok( userService.conditionOne(UserQO.builder().loginName(SpringSecurityUtil.getCurrentUsername()).build()));
    }

    @PutMapping
    public R update(@RequestBody UserQO qo){
        return R.ok( userService.update(qo));
    }


}
