package kr.co.mathrank.init.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.Password;
import kr.co.mathrank.domain.auth.service.MemberRegisterService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitMembers implements CommandLineRunner {
	private final MemberRegisterService memberRegisterService;

	@Override
	public void run(String... args) throws Exception {
		memberRegisterService.register(new MemberRegisterCommand("test1", new Password("0000"), Role.ADMIN));
	}
}
