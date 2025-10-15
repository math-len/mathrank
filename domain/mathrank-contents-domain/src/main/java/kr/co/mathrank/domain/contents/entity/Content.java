package kr.co.mathrank.domain.contents.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(name = "idx_title_createdAt", columnList = "title, created_at desc"),
	@Index(name = "idx_contentType_createdAt", columnList = "content_type, created_at desc"),
	@Index(name = "idx_contentType_title_createdAt", columnList = "content_type, title, created_at desc")
})
public class Content {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	private Long ownerId;

	private String title;
	
	private String text;

	@OneToMany(mappedBy = "content")
	private List<ContentPurchaseLog> contentPurchaseLogs;

	@OneToMany(mappedBy = "content", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<UploadFileInfo> files;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> videoLinks;
	
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	private ContentType contentType;

	@CreationTimestamp
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	public static Content of(final String title, final String text, final List<UploadFileInfo> files, final List<String> videoLinks, final BigDecimal price, final ContentType contentType) {
		final Content content = new Content();

		content.title = title;
		content.text = text;
		setParents(files, content);
		content.files = files;
		content.videoLinks = videoLinks;
		content.price = price;
		content.contentType = contentType;

		return content;
	}

	private static void setParents(final List<UploadFileInfo> uploadFileInfos, final Content content) {
		uploadFileInfos.stream()
			.forEach(uploadFileInfo -> uploadFileInfo.setContent(content));
	}

	public void setFiles(final List<UploadFileInfo> files) {
		this.files.clear();
		files.forEach(uploadFileInfo -> uploadFileInfo.setContent(this));
		this.files.addAll(files);
	}
}
