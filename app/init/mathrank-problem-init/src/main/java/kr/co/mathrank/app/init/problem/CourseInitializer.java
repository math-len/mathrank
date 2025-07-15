package kr.co.mathrank.app.init.problem;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.problem.dto.CourseRegisterCommand;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import kr.co.mathrank.domain.problem.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseInitializer implements CommandLineRunner {
	private final CourseRepository courseRepository;
	private final CourseService courseService;
	@Value("${excel.file.path}")
	private String excelFilePath;

	@Override
	public void run(String... args) throws Exception {
		try (FileInputStream fis = new FileInputStream(excelFilePath);
			 Workbook workbook = new XSSFWorkbook(fis)) {

			final Sheet sheet = workbook.getSheetAt(0);

			// 과정 저장
			final Set<String> set = new HashSet<>();
			for (Row row : sheet) {
				final Cell cell = row.getCell(1);
				if (cell == null) { continue; }


				final String cellName = cell.toString();

				if (set.contains(cellName)) {
					continue;
				}
				set.add(cellName);
				log.info("first: {}", cellName);
				courseService.register(new CourseRegisterCommand(cellName, ""));
			}

			set.clear();
			// 대단원 저장
			for (Row row : sheet) {
				final Cell parentCell = row.getCell(1);
				final Cell cell = row.getCell(2);
				if (cell == null) { continue; }


				final String cellName = cell.toString();

				if (set.contains(cellName)) {
					continue;
				}
				set.add(cellName);
				final Path parentPath = courseRepository.findAllByCourseName(parentCell.toString()).stream()
					.map(Course::getPath)
					.filter(path -> path.getDepth() == 1)
					.findAny()
					.orElseGet(Path::new);
				courseService.register(new CourseRegisterCommand(cellName, parentPath.getPath()));
			}

			set.clear();
			// 중단원
			for (Row row : sheet) {
				final Cell parentCell = row.getCell(2);
				final Cell cell = row.getCell(3);
				if (cell == null) {
					continue;
				}
				final String cellName = cell.toString();

				if (set.contains(cellName)) {
					continue;
				}
				set.add(cellName);
				final Path parentPath = courseRepository.findAllByCourseName(parentCell.toString()).stream()
					.map(Course::getPath)
					.filter(path -> path.getDepth() == 2)
					.findAny()
					.orElseGet(Path::new);
				courseService.register(new CourseRegisterCommand(cellName, parentPath.getPath()));
			}

			// 소단원
			set.clear();
			for (Row row : sheet) {
				final Cell parentCell = row.getCell(3);
				final Cell cell = row.getCell(6);
				if (cell == null) { continue; }


				final String cellName = cell.toString();

				if (set.contains(cellName)) {
					continue;
				}
				set.add(cellName);
				final Path parentPath = courseRepository.findAllByCourseName(parentCell.toString()).stream()
					.map(Course::getPath)
					.filter(path -> path.getDepth() == 3)
					.findAny()
					.orElseGet(Path::new);
				courseService.register(new CourseRegisterCommand(cellName, parentPath.getPath()));
			}
		}

		System.out.println("COUNT: " + courseRepository.count());
	}
}

