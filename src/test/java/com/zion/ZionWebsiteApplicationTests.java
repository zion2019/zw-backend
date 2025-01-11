package com.zion;

import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.learning.service.PushService;
import com.zion.learning.service.TaskService;
import com.zion.resource.user.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZionWebsiteApplicationTests {

	@Resource
	private TaskService taskService;

	@Test
	void contextLoads() {
		taskService.autoFinish();
	}


	@Test
	void push(){
		taskService.scanAndRemind();
	}

	@Resource
	private UserService userService;

	@Test
	void findUser(){
		UserVO userVO = userService.conditionOne(UserQO.builder().id(7267733967850180608L).build());
		System.out.println(userVO);
	}

}
