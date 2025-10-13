package kr.co.mathrank.app.api.contents;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.contents.dto.ContentReadPageQuery;
import kr.co.mathrank.domain.contents.dto.ContentReadPageQueryResult;
import kr.co.mathrank.domain.contents.dto.ContentReadQuery;
import kr.co.mathrank.domain.contents.dto.ContentReadQueryResult;
import kr.co.mathrank.domain.contents.dto.ContentRegisterCommand;
import kr.co.mathrank.domain.contents.dto.ContentUpdateCommand;
import kr.co.mathrank.domain.contents.service.ContentService;
import lombok.RequiredArgsConstructor;

@Tag(name = "자료실 API")
@RestController
@RequiredArgsConstructor
public class ContentsController {
	private final ContentService contentService;

	@PostMapping("/api/v1/content")
	@Operation(summary = "자료 등록 API", description = "관리지(ADMIN)만 사용 가능합니다. 한번 등록된 자료는 삭제할 수 없습니다.")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Long> register(
		@RequestBody @Valid final Requests.ContentRegisterRequest request
	) {
		final ContentRegisterCommand command = request.toCommand();
		final Long contentId = contentService.register(command);

		return ResponseEntity.ok(contentId);
	}

	@PutMapping("/api/v1/content/{contentId}")
	@Operation(summary = "자료 수정 API", description = "관리지(ADMIN)만 사용 가능합니다.")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> update(
		@PathVariable final Long contentId,
		@RequestBody @Valid final Requests.ContentUpdateRequest request
	) {
		final ContentUpdateCommand command = request.toCommand(contentId);
		contentService.update(command);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/v1/contents")
	@Operation(summary = "자료실 페이징 API", description = "로그인된 사용자만 사용 가능합니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<PageResult<ContentReadPageQueryResult>> pageQuery(
		@RequestParam(defaultValue = "20") @Range(min = 1, max = 20) final Integer pageSize,
		@RequestParam(defaultValue = "1") @Range(min = 1, max = 200) final Integer pageNumber,
		@ModelAttribute @ParameterObject final ContentReadPageQuery query
	) {
		final PageResult<ContentReadPageQueryResult> result = contentService.pageQuery(query, pageSize, pageNumber);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/api/v1/content/{contentId}")
	@Operation(summary = "자료 상세 조회 API", description = "이미 구매한 사용자만 사용할 수 있습니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<ContentReadQueryResult> getContent(
		@PathVariable final Long contentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final ContentReadQueryResult result = contentService.read(new ContentReadQuery(contentId, memberPrincipal.memberId(), memberPrincipal.role()));

		return ResponseEntity.ok(result);
	}
}
