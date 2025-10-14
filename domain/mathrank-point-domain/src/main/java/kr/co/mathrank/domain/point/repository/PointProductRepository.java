package kr.co.mathrank.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.point.entity.PointProduct;

public interface PointProductRepository extends JpaRepository<PointProduct, Long> {
}
