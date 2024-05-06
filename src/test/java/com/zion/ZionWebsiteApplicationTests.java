package com.zion;

import com.zion.learning.service.TaskService;
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

}
