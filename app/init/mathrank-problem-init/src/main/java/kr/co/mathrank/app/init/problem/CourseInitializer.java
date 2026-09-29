package kr.co.mathrank.app.init.problem;

import java.io.FileInputStream;

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
	@Value("${excel.course.apply:false}")
	private boolean applyChanges;

	@Override
	public void run(String... args) throws Exception {
		log.info("[CourseInitializer.run] started initialize course by: {}", excelFilePath);
		try (FileInputStream fis = new FileInputStream(excelFilePath);
			 Workbook workbook = new XSSFWorkbook(fis)) {

			final Sheet sheet = workbook.getSheetAt(0);
			final var rows = CourseWorkbookRow.read(sheet);
			log.info("[CourseInitializer.run] parsed valid rows: {}", rows.size());
			if (!applyChanges) {
				log.info("[CourseInitializer.run] dry-run completed. Set excel.course.apply=true to add missing courses.");
				return;
			}

			for (final CourseWorkbookRow row : rows) {
				// main 셀이 존재하면 가져오고 없으면 생성
				final Course mainCourse = courseRepository.findByCourseNameAndPathLength(row.course(), 2).
					orElseGet(() -> {
						final String path = courseService.register(new CourseRegisterCommand(row.course(), ""));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});

				// 대단원 등록
				final Course largeCoursePath = courseRepository.findByCourseNameAndPathStartsWith(
						row.largeUnit(), mainCourse.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(row.largeUnit(), mainCourse.getPath().getPath()));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});

				// 중단원 등록
				final Course midCourse = courseRepository.findByCourseNameAndPathStartsWith(row.middleUnit(),
						largeCoursePath.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(row.middleUnit(), largeCoursePath.getPath().getPath()));
						return courseRepository.findByPath(new Path(path))
							.orElseThrow();
					});
				// 소단원 등록
				courseRepository.findByCourseNameAndPathStartsWith(row.smallUnit(),
						midCourse.getPath().getPath())
					.orElseGet(() -> {
						final String path = courseService.register(
							new CourseRegisterCommand(row.smallUnit(), midCourse.getPath().getPath()));
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
