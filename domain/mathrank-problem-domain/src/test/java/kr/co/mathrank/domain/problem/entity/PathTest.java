package kr.co.mathrank.domain.problem.entity;

import java.util.List;

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

	@Test
	void 부모_경로_추출_테스트() {
		final Path path = new Path("zzaaccdd");
		final List<Path> parents = path.getUpperPaths();

		Assertions.assertAll(
			() -> Assertions.assertEquals(new Path("zz"), parents.get(0)),
			() -> Assertions.assertEquals(new Path("zzaa"), parents.get(1)),
			() -> Assertions.assertEquals(new Path("zzaacc"), parents.get(2)),
			() -> Assertions.assertEquals(3, parents.size())
		);
	}
}