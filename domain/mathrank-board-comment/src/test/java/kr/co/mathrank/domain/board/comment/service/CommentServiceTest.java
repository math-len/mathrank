package kr.co.mathrank.domain.board.comment.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.board.comment.dto.CommentDeleteCommand;
import kr.co.mathrank.domain.board.comment.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.comment.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.comment.entity.Comment;
import kr.co.mathrank.domain.board.comment.repository.CommentRepository;

@SpringBootTest
class CommentServiceTest {
	@Autowired
	private CommentService commentService;
	@Autowired
	private CommentRepository commentRepository;

	@PersistenceContext
	private EntityManager em;

	@Test
	@Transactional
	void 댓글_등록_정상동작_확인() {
		final Long id = commentService.save(new CommentRegisterCommand(1L, 2L, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));

		em.flush();
		em.clear();

		final Comment comment = commentRepository.findById(id)
			.orElseThrow();

		Assertions.assertEquals(1L, comment.getPostId());
		Assertions.assertEquals(2L, comment.getUserId());
		Assertions.assertEquals("테스트 댓글", comment.getContent());
		Assertions.assertEquals(2, comment.getImageSources().size());

		System.out.println("Image Sources: " + comment.getImageSources());
		Assertions.assertTrue(comment.getImageSources().contains("image1.jpg"));
		Assertions.assertTrue(comment.getImageSources().contains("image2.jpg"));
	}

	@Test
	@Transactional
	void 생성_날짜_및_수정_날짜_일치_확인() {
		final Long id = commentService.save(new CommentRegisterCommand(1L, 2L, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));
		em.flush();
		em.clear();

		final Comment comment = commentRepository.findById(id)
			.orElseThrow();
		Assertions.assertNotNull(comment.getCreatedAt(), "생성 날짜가 null이 아닙니다.");
		Assertions.assertNotNull(comment.getUpdatedAt(), "수정 날짜가 null이 아닙니다.");
		Assertions.assertEquals(comment.getCreatedAt(), comment.getUpdatedAt());
	}

	@Test
	@Transactional
	void merge시_수정_날짜_업데이트_된다() {
		final Long id = commentService.save(new CommentRegisterCommand(1L, 2L, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));
		em.flush();
		em.clear();

		final Comment comment = commentRepository.findById(id)
			.orElseThrow();

		comment.setContent("수정된 댓글 내용");
		em.flush();
		em.clear();

		final Comment updatedComment = commentRepository.findById(id)
			.orElseThrow();
		Assertions.assertNotEquals(comment.getCreatedAt(), updatedComment.getUpdatedAt());
	}

	@Test
	@Transactional
	void 영속성_컨텍스트내에서_이미지_수정시_반영_된다() {
		final Long id = commentService.save(new CommentRegisterCommand(1L, 2L, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));

		em.flush();
		em.clear();

		final Comment comment = commentRepository.findById(id)
			.orElseThrow();

		comment.updateImages(List.of("image3.jpg"));
		em.flush();
		em.clear();

		final Comment updatedComment = commentRepository.findById(id)
			.orElseThrow();
		Assertions.assertTrue(updatedComment.getImageSources().contains("image3.jpg"));
		Assertions.assertEquals(1, updatedComment.getImageSources().size());
	}

	@Test
	@Transactional
	void 소유자만_수정가능() {
		final long userId = 2L;
		final long otherId = 3L;
		final Long id = commentService.save(new CommentRegisterCommand(1L,
			userId, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));
		em.flush();
		em.clear();

		final String updatedContent = "수정된 댓글 내용";
		final String updatedImageSource = "image3.jpg";

		Assertions.assertThrows(IllegalArgumentException.class, () -> commentService.update(new CommentUpdateCommand(id, otherId, updatedContent, List.of(updatedImageSource))));

	}

	@Test
	@Transactional
	void 수정_정상동작_확인() {
		final long userId = 2L;
		final Long id = commentService.save(new CommentRegisterCommand(1L,
			userId, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));
		em.flush();
		em.clear();

		final String updatedContent = "수정된 댓글 내용";
		final String updatedImageSource = "image3.jpg";
		commentService.update(new CommentUpdateCommand(id, userId, updatedContent, List.of(updatedImageSource)));

		em.flush();
		em.clear();

		final Comment updatedComment = commentRepository.findById(id)
			.orElseThrow();

		Assertions.assertEquals(updatedContent, updatedComment.getContent());
		Assertions.assertTrue(updatedComment.getImageSources().contains(updatedImageSource));
		Assertions.assertEquals(1, updatedComment.getImageSources().size());
	}

	@Test
	@Transactional
	void 소유자만_삭제가능() {
		final long userId = 2L;
		final long otherId = 3L;
		final Long id = commentService.save(new CommentRegisterCommand(1L,
			userId, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));
		em.flush();
		em.clear();

		Assertions.assertThrows(IllegalArgumentException.class, () -> commentService.delete(new CommentDeleteCommand(id, otherId)));
	}

	@Test
	@Transactional
	void 삭제_정상동작_확인() {
		final long userId = 2L;
		final Long id = commentService.save(new CommentRegisterCommand(1L,
			userId, "테스트 댓글", List.of("image1.jpg", "image2.jpg")));

		em.flush();
		em.clear();

		commentService.delete(new CommentDeleteCommand(id, userId));

		em.flush();
		em.clear();

		Assertions.assertEquals(0, commentRepository.count());
	}
}
