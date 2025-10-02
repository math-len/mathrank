package kr.co.mathrank.domain.auth.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberDeleteCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.MemberType;
import kr.co.mathrank.domain.auth.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberDeleteServiceTest {
	@Autowired
	private MemberDeleteService deleteService;
	@MockitoBean
	private MemberRepository memberRepository;

	@Test
	void 삭제_대상_아이디와_요청_아이디가_다르면_예외() {
		Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createMember()));
		Assertions.assertThrows(ConstraintViolationException.class,
			() -> deleteService.delete(new MemberDeleteCommand(1L, 2L, Role.USER)));
	}

	@Test
	void 본인_아이디_삭제면_가능() {
		Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createMember()));
		Assertions.assertDoesNotThrow(() -> deleteService.delete(new MemberDeleteCommand(1L, 1L, Role.USER)));
	}

	@Test
	void 어드민이면_다른_아이디_삭제_가능() {
		Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createMember()));
		Assertions.assertDoesNotThrow(() -> deleteService.delete(new MemberDeleteCommand(1L, 2L, Role.ADMIN)));
	}

	private Member createMember() {
		return Member.of(1L, "test", Role.USER, "1L", "test", MemberType.NORMAL, true, "2321");
	}
}
