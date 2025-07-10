package kr.co.mathrank.domain.problem.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
final class PageUtil {
	private static final int MAX_NEXT_PAGE_COUNT = 10;

	public static List<Integer> getNextPages(final int queryPageSize, final int queryPageNumber, final long totalCount, final int queryResultCount) {
		final long nextPageCount = getNextPageCount(queryPageSize, queryPageNumber, totalCount, queryResultCount);
		return getNextPages(queryPageNumber, nextPageCount);
	}

	public static long getNextPageCount(int queryPageSize, int queryPageNumber, long totalCount, int queryResultCount) {
		final long nextElementCount = totalCount - getPassedElementCount(queryPageSize, queryPageNumber, queryResultCount);
		if (nextElementCount <= 0) {
			return 0;
		}
		final long nextPageCount = Math.ceilDiv(nextElementCount, queryPageSize);
		log.debug("[PageUtil.nextPageCount] nextElementCount: {}, queryPageSize: {}, nextPageCount: {}", nextElementCount, queryPageSize, nextPageCount);
		return nextPageCount;
	}

	public static long getPassedElementCount(int queryPageSize, int queryPageNumber, int queryResultCount) {
		if (queryPageSize <= 0 || queryPageNumber <= 0 || queryResultCount < 0) {
			throw new IllegalArgumentException("Invalid arguments: queryPageSize, queryPageNumber, and queryResultCount must be positive.");
		}

		final long passedElementCount = (long) queryPageSize * (queryPageNumber - 1) + queryResultCount;
		log.debug("[PageUtil.passedElementCount]: {}", passedElementCount);
		return passedElementCount;
	}

	private static List<Integer> getNextPages(final int currentPageNumber, final long nextPageCount) {
		if (nextPageCount <= 0) {
			return Collections.emptyList();
		}
		int endPageNumber;
		if (nextPageCount >= MAX_NEXT_PAGE_COUNT) {
			endPageNumber = currentPageNumber + MAX_NEXT_PAGE_COUNT;
		} else {
			endPageNumber = currentPageNumber + (int) nextPageCount;
		}
		log.debug("[PageUtil.nextPages] startPageNumber: {}, endPageNumber: {}", currentPageNumber, endPageNumber);
		return IntStream.rangeClosed(currentPageNumber + 1, endPageNumber)
			.boxed()
			.toList();
	}
}
