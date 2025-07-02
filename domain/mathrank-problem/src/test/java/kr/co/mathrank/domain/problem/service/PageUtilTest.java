package kr.co.mathrank.domain.problem.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	properties = """
logging.level.kr.co.mathrank=DEBUG
"""
)
class PageUtilTest {
	@Test
	void 음수는_허용하지_않는다() {
		// 페이지 크기, 조회한 페이지 번호, 조회된 갯수는 음수일 수 없다.
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getPassedElementCount(-1, 1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getPassedElementCount(10, -1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getPassedElementCount(10, 1, -1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPages(-10, 1, 1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPages(10, -1, 1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPages(10, 1, 1, -1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPageCount(-10, 1, 1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPageCount(10, -1, 1, 1));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PageUtil.getNextPageCount(10, 1, 1, -1));
	}

	@Test
	void 이미_조회한_갯수() {
		// 페이지 크기가 10개이고 조회한 페이지 number가 5일 때,
		// 조회 결과가 5이면
		// 지금까지 조회한 갯수는 45개이다.
		final Long count = PageUtil.getPassedElementCount(10, 5, 5);
		assertEquals(45, count);
	}

	@Test
	void 다음_페이지_갯수() {
		// 10개씩 1페이지를 조회했을때, 조회된 갯수가 10개이고 전체 갯수가 100개이다.
		// 이때 다음 페이지의 갯수는 9개이다.
		final long nextPagesCount = PageUtil.getNextPageCount(10, 1, 100, 10);
		Assertions.assertEquals(9, nextPagesCount);

		// ...  전체갯수가 101개이다.
		// 이때 다음 페이지의 갯수는 10개이다.
		final long nextPagesCount1 = PageUtil.getNextPageCount(10, 1, 101, 10);
		Assertions.assertEquals(10, nextPagesCount1);
	}

	@Test
	void 다음페이지는_현재_조회페이지의_다음페이지() {
		// 현재 페이지가 1일때, 결과는 2 부터
		Assertions.assertEquals(2, PageUtil.getNextPages(10, 1, 100, 10).getFirst());
	}

	@Test
	void 다음_페이지의_최대_갯수는_10개이다() {
		// 전체갯수가 1000개
		final List<Integer> nextPages = PageUtil.getNextPages(10, 1, 1000, 10);
		Assertions.assertEquals(10, nextPages.size());

		// 전체갯수가 100개
		// 현재 페이지를 제외하여 9개
		final List<Integer> nextPages1 = PageUtil.getNextPages(10, 1, 100, 10);
		Assertions.assertEquals(9, nextPages1.size());
	}
}