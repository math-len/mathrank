package kr.co.mathrank.app.api.auth.cookie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	properties = "cookie.mode=develop"
)
class DevelopCookieManagerTest {
	@Autowired
	private BeanFactory beanFactory;

	@Test
	void 쿠키_프로퍼티가_부재할떄_배포환경_쿠키_매니저가_등록된다() {
		Assertions.assertNotNull(beanFactory.getBean(DevelopCookieManager.class));
		Assertions.assertThrows(
			NoSuchBeanDefinitionException.class, () -> {beanFactory.getBean(DeployCookieManager.class);
			});
	}
}