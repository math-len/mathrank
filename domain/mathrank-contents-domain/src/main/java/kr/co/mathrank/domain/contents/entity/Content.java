package kr.co.mathrank.domain.contents.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Content {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	
	private String text;

	private String content;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> fileSources;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> videoLinks;
	
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	private ContentType contentType;

	@CreationTimestamp
	private LocalDateTime createdAt;

	public static Content of(final String title, final String text, final List<String> fileSources, final List<String> videoLinks, final BigDecimal price, final ContentType contentType) {
		final Content content = new Content();
		content.title = title;
		content.text = text;
		content.fileSources = fileSources;
		content.videoLinks = videoLinks;
		content.price = price;
		content.contentType = contentType;

		return content;
	}
}
