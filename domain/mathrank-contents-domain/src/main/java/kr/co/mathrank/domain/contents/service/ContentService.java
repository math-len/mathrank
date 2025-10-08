package kr.co.mathrank.domain.contents.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.contents.dto.ContentRegisterCommand;
import kr.co.mathrank.domain.contents.dto.ContentUpdateCommand;
import kr.co.mathrank.domain.contents.entity.Content;
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
		content.setFileSources(command.fileSources());
		content.setVideoLinks(command.videoLinks());
	}
}
