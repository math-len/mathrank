package kr.co.mathrank.init.auth;

import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.MemberType;
import kr.co.mathrank.domain.auth.entity.Password;
import kr.co.mathrank.domain.auth.service.MemberRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitMembers implements CommandLineRunner {
	private final MemberRegisterService memberRegisterService;

	@Override
	public void run(String... args) throws Exception {
		final String loginId = "test1";
		final String password = "0000";
		memberRegisterService.register(new MemberRegisterCommand("test1", "testName", new Password("0000"), Role.ADMIN, MemberType.NORMAL, true,
			null));
		log.info("[InitMembers.run] initialized test member - loginId: {}, password: {}", loginId, password);
	}
}
