package kr.co.mathrank.app.api.board;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.board.dto.CommentDeleteCommand;
import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.dto.PostDetailQueryResult;
import kr.co.mathrank.domain.board.dto.PostPageQuery;
import kr.co.mathrank.domain.board.dto.PostPageQueryResult;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.service.CommentDeleteService;
import kr.co.mathrank.domain.board.service.CommentRegisterService;
import kr.co.mathrank.domain.board.service.CommentUpdateService;
import kr.co.mathrank.domain.board.service.PostDeleteService;
import kr.co.mathrank.domain.board.service.PostQueryService;
import kr.co.mathrank.domain.board.service.PostRegisterService;
import kr.co.mathrank.domain.board.service.PostUpdateService;
import lombok.RequiredArgsConstructor;

@Tag(name = "게시판 API")
@RestController
@RequiredArgsConstructor
public class BoardController {
	private final PostRegisterService postRegisterService;
	private final PostUpdateService postUpdateService;
	private final PostDeleteService postDeleteService;
	private final PostQueryService postQueryService;

	private final MemberClient memberClient;
	private final CommentRegisterService commentRegisterService;
	private final CommentUpdateService commentUpdateService;
	private final CommentDeleteService commentDeleteService;

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

	@Operation(summary = "게시글 수정 API")
	@PutMapping("/api/v1/board/post/{postId}")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> update(
		@ModelAttribute @ParameterObject @Valid final Requests.PostUpdateRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final PostUpdateCommand command = request.toCommand(memberPrincipal.memberId());
		postUpdateService.update(command);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "게시글 삭제 API")
	@DeleteMapping("/api/v1/board/post/{postId}")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> delete(
		@PathVariable final Long postId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final PostDeleteCommand command = new PostDeleteCommand(postId, memberPrincipal.memberId(),
			memberPrincipal.role());
		postDeleteService.delete(command);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "페이지 조회 API", description = "정렬은 날짜 내림차순으로 적용됩니다.")
	@GetMapping("/api/v1/board/post")
	public ResponseEntity<PageResult<PostPageQueryResult>> pageQuery(
		@RequestParam(defaultValue = "1") @Range(min = 1, max = 1000) final Integer pageNumber,
		@RequestParam(defaultValue = "20") @Range(min = 1, max = 20) final Integer pageSize,
		@ModelAttribute @ParameterObject final PostPageQuery query
	) {
		return ResponseEntity.ok(postQueryService.pageQuery(query, pageSize, pageNumber));
	}

	@Operation(summary = "게시글 상세 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/board/post/{postId}")
	public ResponseEntity<Responses.PostDetailResponse> getDetail(
		@PathVariable final Long postId
	) {
		final PostDetailQueryResult result = postQueryService.getDetail(postId);

		// 댓글 매핑
		final List<Responses.CommentDetailResponse> commentDetailResponses = result.comments().stream()
			.map(comment -> Responses.CommentDetailResponse.from(comment,
				memberClient.getMemberInfo(comment.memberId())))
			.toList();

		return ResponseEntity.ok(Responses.PostDetailResponse.from(result, commentDetailResponses));
	}

	@Operation(summary = "댓글 작성 API")
	@Authorization(openedForAll = true)
	@PostMapping("/api/v1/board/post/{postId}/comment")
	public ResponseEntity<Long> postComment(
		@ModelAttribute @ParameterObject @Valid final Requests.CommentSaveRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final CommentRegisterCommand command = request.toCommand(memberPrincipal.memberId());
		final Long commentId = commentRegisterService.register(command);

		return ResponseEntity.ok(commentId);
	}

	@Operation(summary = "댓글 수정 API", description = "본인이 작성한 댓글만 수정 가능. 관리자도 수정 불가")
	@Authorization(openedForAll = true)
	@PutMapping("/api/v1/board/post/comment/{commentId}")
	public ResponseEntity<Void> postComment(
		@ModelAttribute @ParameterObject @Valid final Requests.CommentUpdateRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final CommentUpdateCommand command = request.toCommand(memberPrincipal.memberId());
		commentUpdateService.update(command);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "댓글 삭제 API", description = "본인, 관리자가 삭제 가능")
	@Authorization(openedForAll = true)
	@DeleteMapping("/api/v1/board/post/comment/{commentId}")
	public ResponseEntity<Void> deleteComment(
		@PathVariable final Long commentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final CommentDeleteCommand command = new CommentDeleteCommand(commentId, memberPrincipal.memberId(),
			memberPrincipal.role());
		commentDeleteService.delete(command);

		return ResponseEntity.ok().build();
	}
}
