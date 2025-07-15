package kr.co.mathrank.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.MemberInfoResult;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class MemberQueryService {
	private final MemberRepository memberRepository;

	/**
	 * @param memberId
	 * @return never returns null
	 */
	public MemberInfoResult getInfo(@NotNull final Long memberId) {
		return memberRepository.findById(memberId)
			.map(MemberInfoResult::from)
			.orElseGet(MemberInfoResult::none);
	}
}
