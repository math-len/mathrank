package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

public interface SingleProblemReadModelRepository
	extends JpaRepository<SingleProblemReadModel, Long>, SingleProblemReadModelQueryRepository {
	@Query("SELECT sp FROM SingleProblemReadModel sp WHERE sp.problemId = :problemId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SingleProblemReadModel> findByProblemIdForUpdate(@Param("problemId") Long problemId);
}
