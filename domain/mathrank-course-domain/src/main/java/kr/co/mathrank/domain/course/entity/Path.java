package kr.co.mathrank.domain.course.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Path {
	private String path = MIN_CHUNK;

	private static final String SEQUENCE = "abcdefghijklmnopqrstuvwxyz";

	private static final int DEPTH_CHUNK_SIZE = 2;
	private static final int MAX_CHUNK_COUNT = 4;

	// MIN_CHUNK = "aa", MAX_CHUNK = "zz"
	private static final String MIN_CHUNK = String.valueOf(SEQUENCE.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
	private static final String MAX_CHUNK = String.valueOf(SEQUENCE.charAt(SEQUENCE.length() - 1))
		.repeat(DEPTH_CHUNK_SIZE);

	public Path(final String path) {
		this.path = path;
		if (getDepth() > MAX_CHUNK_COUNT) {
			log.warn("[Path.new] over max chunk count (curr: {}, max: {})", getDepth(), MAX_CHUNK_COUNT);
			throw new IllegalStateException("over max chunk count");
		}
	}

	public int getDepth() {
		return this.path.length() / DEPTH_CHUNK_SIZE;
	}

	public int getChildDepth() {
		return getDepth() + 1;
	}

	public int getChildLength() {
		return getChildDepth() * DEPTH_CHUNK_SIZE;
	}

	/**
	 * @param latestPath 해당 {@code Path}를 거치는 모든 경로를 오름차순 정렬했을때, 가장 마지막의 {@code Path}
	 * @return 유일한 자식경로
	 * 현재 존재하는 자식경로 중, 가장 최신에 생성된 경로를 인자로 받는다.
	 * 이를 통해, 유일한 자식경로을 생성한다.
	 */
	public Path nextChild(final Path latestPath) {
		if (this.path.equals(latestPath.path)) {
			return new Path(this.path + MIN_CHUNK);
		}

		final String rightNextChunk = parseRightNextChunk(latestPath);
		final String nextChunk = nextChunk(rightNextChunk);

		return new Path(this.path + nextChunk);
	}

	@Transient
	public List<Path> getUpperPaths() {
		final int currentPathDepth = getDepth();
		final List<Path> parents = new ArrayList<>();
		for (int i = 1; i < currentPathDepth; i++) {
			parents.add(new Path(this.path.substring(0, i * DEPTH_CHUNK_SIZE)));
		}
		return parents;
	}

	private String nextChunk(final String chunk) {
		if (chunk.equals(MAX_CHUNK)) {
			throw new IllegalStateException("Max chunk length is " + MAX_CHUNK);
		}
		final int currentValue = getTotal(chunk);
		return getSequence(currentValue + 1);
	}

	private String parseRightNextChunk(final Path latestPath) {
		return latestPath.path.substring(this.path.length(), this.path.length() + DEPTH_CHUNK_SIZE);
	}

	private int getTotal(final String chunk) {
		final int length = chunk.length();
		int sum = 0;
		for (int i = 0; i < chunk.length(); i++) {
			final int value = getValue(chunk.charAt(i));
			final int multiplyValue = (int)Math.pow(SEQUENCE.length(), length - i - 1);

			sum += value * multiplyValue;
		}

		return sum;
	}

	private String getSequence(int value) {
		if (value < 0 || value >= Math.pow(SEQUENCE.length(), DEPTH_CHUNK_SIZE)) {
			throw new IllegalArgumentException(
				"value must be between 0 and " + (int)Math.pow(SEQUENCE.length(), DEPTH_CHUNK_SIZE) + " exclusive");
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
			int div = (int)Math.pow(SEQUENCE.length(), DEPTH_CHUNK_SIZE - i - 1);
			int index = value / div;
			sb.append(SEQUENCE.charAt(index));
			value %= div;
		}

		return sb.toString();
	}

	private int getValue(final char target) {
		return SEQUENCE.indexOf(target);
	}
}

