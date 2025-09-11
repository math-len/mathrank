package kr.co.mathrank.domain.image;

public interface ImageStorage {
	void store(UploadFile uploadFile);
	void delete(String fullFileName);
	ImageFileResult load(String fullFileName);
}
