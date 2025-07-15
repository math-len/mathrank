package kr.co.mathrank.app.api.auth.cookie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	properties = "cookie.mode=deploy"
)
class DeployCookieManagerTest {
	@Autowired
	private BeanFactory beanFactory;

	@Test
	void 프로퍼티설정이_deploy일떄_등록된다() {
		Assertions.assertNotNull(beanFactory.getBean(DeployCookieManager.class));
		Assertions.assertThrows(
			NoSuchBeanDefinitionException.class, () -> {beanFactory.getBean(DevelopCookieManager.class);
			});
	}
}