package kr.co.mathrank.app.api.board;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
import kr.co.mathrank.domain.board.service.PostRegisterService;
import lombok.RequiredArgsConstructor;

@Tag(name = "게시판 API")
@RestController
@RequiredArgsConstructor
public class BoardController {
	private final PostRegisterService postRegisterService;

	@Operation(summary = "게시글 등록 API")
	@PostMapping("/api/v1/board/post")
	@Authorization(openedForAll = true)
	public ResponseEntity<Long> save(
		@ModelAttribute @ParameterObject @Valid final Requests.PostSaveRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final PostRegisterCommand command = request.toCommand(memberPrincipal.memberId(), memberPrincipal.role());
		final Long postId = postRegisterService.register(command);

		return ResponseEntity.ok(postId);
	}
}
