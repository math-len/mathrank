package kr.co.mathrank.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.auth.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
