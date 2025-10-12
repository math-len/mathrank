package kr.co.mathrank.domain.contents.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.contents.dto.ContentReadPageQuery;
import kr.co.mathrank.domain.contents.dto.ContentReadPageQueryResult;
import kr.co.mathrank.domain.contents.dto.ContentReadQuery;
import kr.co.mathrank.domain.contents.dto.ContentReadQueryResult;
import kr.co.mathrank.domain.contents.dto.ContentRegisterCommand;
import kr.co.mathrank.domain.contents.dto.ContentUpdateCommand;
import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.exception.CannotFoundContentException;
import kr.co.mathrank.domain.contents.exception.NotPurchsedContentException;
import kr.co.mathrank.domain.contents.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ContentService {
	private final ContentRepository contentRepository;

	public Long register(@NotNull @Valid final ContentRegisterCommand command) {
		final Content content = command.toEntity();
		contentRepository.save(content);
		log.info("[ContentService.register] content register success - contentId: {}", content.getId());

		return content.getId();
	}

	@Transactional
	public void update(@NotNull @Valid final ContentUpdateCommand command) {
		final Content content = contentRepository.findById(command.contentId())
			.orElseThrow();
		content.setTitle(command.title());
		content.setText(command.text());
		content.setPrice(command.price());
		content.setFiles(command.getUploadFileInfos());
		content.setVideoLinks(command.videoLinks());
	}

	/**
	 * 지불한 사용자만 이용 가능합니다.
	 * @param command
	 * @return
	 */
	@Transactional
	public ContentReadQueryResult read(@NotNull @Valid final ContentReadQuery command) {
		final Content content = findContentWithPurchasedUsers(command.contentId());
		validate(content, command.userId(), command.role());

		return ContentReadQueryResult.from(content);
	}

	public PageResult<ContentReadPageQueryResult> pageQuery(
		@NotNull @Valid final ContentReadPageQuery pageQuery,
		final int pageSize,
		final int pageNumber
	) {
		final List<Content> contents = contentRepository.queryPage(pageQuery, pageSize, pageNumber - 1);
		final Long count = contentRepository.count(pageQuery);

		return PageResult.of(contents, pageNumber, pageSize, PageUtil.getNextPages(pageSize, pageNumber, count, contents.size()))
			.map(ContentReadPageQueryResult::from);
	}

	private Content findContentWithPurchasedUsers(final Long contentId) {
		return contentRepository.findContentWithPurchasedUsers(contentId)
			.orElseThrow(() -> {
				log.info("[ContentService.read] content not purchased - contentId: {}", contentId);
				return new CannotFoundContentException();
			});
	}

	private void validate(final Content content, final Long userId, final Role role) {
		// 관리자는 다 조회 가능
		if (role == Role.ADMIN) {
			return;
		}

		// 일반 사용자는 결제해야지 조회 가능
		if (content.getContentUsers().stream().anyMatch(user -> user.getUserId().equals(userId))) {
			return;
		}

		log.info("[ContentService.read] content not purchased - contentId: {}, userId: {}", content.getId(), userId);
		throw new NotPurchsedContentException();
	}
}
