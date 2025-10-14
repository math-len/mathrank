package kr.co.mathrank.domain.contents.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class UploadFileInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@Setter
	private Content content;

	private String fileName;

	private String fileSource;

	public static UploadFileInfo of(String fileName, String fileSource) {
		final UploadFileInfo uploadFileInfo = new UploadFileInfo();
		uploadFileInfo.fileName = fileName;
		uploadFileInfo.fileSource = fileSource;

		return uploadFileInfo;
	}
}
