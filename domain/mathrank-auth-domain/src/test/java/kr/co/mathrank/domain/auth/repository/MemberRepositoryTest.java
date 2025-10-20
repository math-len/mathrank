package kr.co.mathrank.domain.auth.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void oAuth_제공자와_ID로_조회성공() {
		final String oAuthId = "12";
		final Member member = Member.fromOAuth(1L, oAuthId, OAuthProvider.KAKAO, "nickName", Role.USER);
		memberRepository.save(member);

		entityManager.flush();
		entityManager.clear();

		Assertions.assertTrue(memberRepository.findByOAuthIdAndProvider(oAuthId, OAuthProvider.KAKAO).isPresent());
	}
}
