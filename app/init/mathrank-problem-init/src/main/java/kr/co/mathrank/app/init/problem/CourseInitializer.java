package kr.co.mathrank.app.init.problem;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.course.dto.CourseRegisterCommand;
import kr.co.mathrank.domain.course.entity.Course;
import kr.co.mathrank.domain.course.entity.Path;
import kr.co.mathrank.domain.course.repository.CourseRepository;
import kr.co.mathrank.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("init-course")
@RequiredArgsConstructor
public class CourseInitializer implements CommandLineRunner {
	private final CourseRepository courseRepository;
	private final CourseService courseService;
	@Value("${excel.file.path}")
	private String excelFilePath;

	@Override
	public void run(String... args) throws Exception {
		log.info("[CourseInitializer.run] started initialize course by: {}", excelFilePath);
		try (FileInputStream fis = new FileInputStream(excelFilePath);
			 Workbook workbook = new XSSFWorkbook(fis)) {

			final Sheet sheet = workbook.getSheetAt(0);

			for (final Row row : sheet) {
				final Cell mainCell = row.getCell(1); // 과정
				final Cell lageUnitCell = row.getCell(2); // 대단원
				final Cell midUnitCell = row.getCell(3); // 중단원
				final Cell littleUnitCell = row.getCell(6); // 소단원

				if (mainCell == null || lageUnitCell == null || midUnitCell == null || littleUnitCell == null) {
					break;
				}

				// main 셀이 존재하면 가져오고 없으면 생성
				final Course mainCourse = courseRepository.findByCourseNameAndPathLength(mainCell.toString(), 2).
					orElseGet(() -> {
						final String path = courseService.register(new CourseRegisterCommand(mainCell.toString(), ""));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});

				// 대단원 등록
				final Course largeCoursePath = courseRepository.findByCourseNameAndPathStartsWith(
						lageUnitCell.toString(), mainCourse.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(lageUnitCell.toString(), mainCourse.getPath().getPath()));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});

				// 중단원 등록
				final Course midCourse = courseRepository.findByCourseNameAndPathStartsWith(midUnitCell.toString(),
						largeCoursePath.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(midUnitCell.toString(), largeCoursePath.getPath().getPath()));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});
				// 소단원 등록
				courseRepository.findByCourseNameAndPathStartsWith(littleUnitCell.toString(),
						midCourse.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(littleUnitCell.toString(), midCourse.getPath().getPath()));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});
			}
		} catch (Exception e) {
			log.warn("[CourseInitializer.run] error occurred in initialize: {}", excelFilePath, e);
			return;
		}

		log.info("[CourseInitializer.run] initialized completed - total count: {}", courseRepository.count());
	}
}

