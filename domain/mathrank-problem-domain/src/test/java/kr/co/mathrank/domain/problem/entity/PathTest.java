package kr.co.mathrank.domain.problem.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PathTest {
	@Test
	void 자식_생성_확인() {
		final Path path = new Path("az");
		final Path latestPath = new Path("azaz");

		Assertions.assertEquals("azba", path.nextChild(latestPath).getPath());
	}

	@Test
	void 자식_없을때_생성한다() {
		final Path path = new Path("az");
		final Path latestPath = new Path("az");

		Assertions.assertEquals("azaa", path.nextChild(latestPath).getPath());
	}

	@Test
	void 다음_레벨의_자식패스를_리턴한다() {
		final Path path = new Path("az");
		final Path latestPath = new Path("azazaz");

		Assertions.assertEquals("azba", path.nextChild(latestPath).getPath());
	}

	@Test
	void 레벨별_맥스를_넘으면_에러() {
		final Path path = new Path("zz");
		final Path latestPath = new Path("zzzz");

		Assertions.assertThrows(IllegalStateException.class, () -> path.nextChild(latestPath));
	}
}