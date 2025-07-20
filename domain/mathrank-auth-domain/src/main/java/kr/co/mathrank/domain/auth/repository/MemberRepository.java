package kr.co.mathrank.domain.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByLoginId(String loginId);

	@Query("SELECT m FROM Member m WHERE m.oAuthInfo.oAuthUserId = :oAuthId AND m.oAuthInfo.oAuthProvider = :provider")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Member> findByOAuthIdAndProvider(
		@Param("oAuthId") final Long oAuthInfoOAuthUserId,
		@Param("provider") final OAuthProvider provider
	);

	@Query("SELECT m FROM Member m WHERE m.oAuthInfo.oAuthUserId = :oAuthId AND m.oAuthInfo.oAuthProvider = :provider")
	List<Member> findAllByOAuthIdAndProviderNoLock(
		@Param("oAuthId") final Long oAuthInfoOAuthUserId,
		@Param("provider") final OAuthProvider provider
	);
}
