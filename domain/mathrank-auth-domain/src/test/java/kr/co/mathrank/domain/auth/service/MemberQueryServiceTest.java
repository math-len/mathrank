package kr.co.mathrank.domain.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberInfoResult;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberQueryServiceTest {
	@Autowired
	private MemberQueryService memberQueryService;
	@Autowired
	private MemberRepository memberRepository;

	@Test
	void 존재하는_멤버_조회_성공_테스트() {
		final long memberId = 1L;
		final Member member = Member.of(memberId, "testName", Role.USER, "testId", "testPassword");
		memberRepository.save(member);

		Assertions.assertEquals(MemberInfoResult.from(member), memberQueryService.getInfo(memberId));
	}

	@Test
	void 존재하지않는_멤버_조회시_널을_리턴하지않고_빈값을_리턴한다() {
		final long memberId = 1L;

		Assertions.assertEquals(MemberInfoResult.none(), memberQueryService.getInfo(memberId));
	}
}