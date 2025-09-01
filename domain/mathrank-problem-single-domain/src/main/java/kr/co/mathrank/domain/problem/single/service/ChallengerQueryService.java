package kr.co.mathrank.domain.problem.single.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.dto.ChallengerQueryResults;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemChallengeLogResults;
import kr.co.mathrank.domain.problem.single.exception.CannotFindChallengerException;
import kr.co.mathrank.domain.problem.single.repository.ChallengerRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ChallengerQueryService {
	private final ChallengerRepository challengerRepository;

	public ChallengerQueryResults findMemberChallenges(@NotNull final Long memberId) {
		return new ChallengerQueryResults(challengerRepository.findByMemberId(memberId));
	}

	public SingleProblemChallengeLogResults findChallengeLogs(
		@NotNull final Long requestMemberId,
		@NotNull final Long singleProblemId
	) {
		return SingleProblemChallengeLogResults.from(
			challengerRepository.findByMemberIdAndSingleProblemId(requestMemberId, singleProblemId)
				.orElseThrow(CannotFindChallengerException::new)
				.getChallengeLogs());
	}
}
