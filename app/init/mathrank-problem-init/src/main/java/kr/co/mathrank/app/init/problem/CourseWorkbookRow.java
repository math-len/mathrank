package kr.co.mathrank.app.init.problem;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

record CourseWorkbookRow(String course, String largeUnit, String middleUnit, String smallUnit) {
	static List<CourseWorkbookRow> read(final Sheet sheet) {
		final DataFormatter formatter = new DataFormatter();
		final List<CourseWorkbookRow> rows = new ArrayList<>();
		for (final Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			final String course = formatter.formatCellValue(row.getCell(1)).trim();
			final String largeUnit = formatter.formatCellValue(row.getCell(2)).trim();
			final String middleUnit = formatter.formatCellValue(row.getCell(3)).trim();
			final String smallUnit = formatter.formatCellValue(row.getCell(6)).trim();
			if (course.isEmpty() || largeUnit.isEmpty() || middleUnit.isEmpty() || smallUnit.isEmpty()) {
				continue;
			}
			rows.add(new CourseWorkbookRow(course, largeUnit, middleUnit, smallUnit));
		}
		return rows;
	}
}
