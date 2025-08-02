package kr.co.mathrank.domain.problem.single.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.AlreadyRegisteredProblemException;
import kr.co.mathrank.domain.problem.single.exception.CannotRegisterWithThisRoleException;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemService {
	private final SingleProblemRepository singleProblemRepository;

	/**
	 * 문제를 개별문제로 등록하기 위한 API입니다.
	 * @param command
	 */
	public void register(@NotNull @Valid final SingleProblemRegisterCommand command) {
		// 관리자만 문제를 개별문제로 등록 가능하다
		if (command.role() != Role.ADMIN) {
			log.warn("[SingleProblemService.register] cannot register single problem with this user - userId:{}, userRole: {}", command.memberId(), command.role());
			throw new CannotRegisterWithThisRoleException();
		}

		final SingleProblem problem = SingleProblem.of(command.problemId(), command.memberId());
		try {
			singleProblemRepository.save(problem);
		} catch (DataIntegrityViolationException e) {
			// 이미 등록된 문제는 다시 등록할 수 없다.
			log.warn("[SingleProblemService.register] cannot register single problem duplicated: {}", command.problemId());
			throw new AlreadyRegisteredProblemException();
		}

		log.info("[SingleProblemService.register] single problem registered - singleProblemId: {}, problemId: {}", problem.getId(), problem.getProblemId());
	}
}
