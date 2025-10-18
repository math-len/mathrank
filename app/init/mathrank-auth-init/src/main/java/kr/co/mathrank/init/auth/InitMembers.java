package kr.co.mathrank.init.auth;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
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
@Profile("init-member")
@RequiredArgsConstructor
public class InitMembers implements CommandLineRunner {
	private final MemberRegisterService memberRegisterService;
	private final AdminAccountProperties adminAccountProperties;

	@Override
	public void run(String... args) throws Exception {
		final Map<String, AdminAccountProperties.Account> propertiesMap = adminAccountProperties.getAccount();

		for (final Map.Entry<String, AdminAccountProperties.Account> entry : propertiesMap.entrySet()) {
			final String userName = entry.getKey();
			final String loginId = entry.getValue().getLoginId();
			final String password = entry.getValue().getPassword();

			memberRegisterService.register(new MemberRegisterCommand(loginId, userName, new Password(password), Role.ADMIN, MemberType.NORMAL, true,
				null));
			log.info("[InitMembers.run] initialized test member - loginId: {}, password: ****", loginId);
		}

		log.info("[InitMembers.run] initialized test member - count: {}", propertiesMap.size());
	}
}
