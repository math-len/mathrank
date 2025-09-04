package kr.co.mathrank.domain.single.problem.rank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.single.problem.rank.entity.Solver;

public interface SolverRepository extends JpaRepository<Solver, Long> {
}
