package kr.co.mathrank.app.init.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class CourseWorkbookRowTest {
	@Test
	void 헤더와_빈_행을_건너뛰고_유효한_단원만_읽는다() throws Exception {
		try (final XSSFWorkbook workbook = new XSSFWorkbook()) {
			final var sheet = workbook.createSheet("기존+개정");
			final var header = sheet.createRow(0);
			header.createCell(1).setCellValue("과정");
			header.createCell(2).setCellValue("대단원");
			header.createCell(3).setCellValue("중단원");
			header.createCell(6).setCellValue("소단원");
			final var valid = sheet.createRow(2);
			valid.createCell(1).setCellValue("중1");
			valid.createCell(2).setCellValue("수와 연산");
			valid.createCell(3).setCellValue("소인수분해");
			valid.createCell(6).setCellValue("소수와 합성수");

			final var rows = CourseWorkbookRow.read(sheet);

			assertEquals(1, rows.size());
			assertEquals("중1", rows.getFirst().course());
			assertEquals("소수와 합성수", rows.getFirst().smallUnit());
		}
	}
}
