package kr.co.mathrank.domain.point.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.point.dto.PointProductCreateCommand;
import kr.co.mathrank.domain.point.dto.PointProductDeleteCommand;
import kr.co.mathrank.domain.point.dto.PointProductQueryResult;
import kr.co.mathrank.domain.point.dto.PointProductQueryResults;
import kr.co.mathrank.domain.point.entity.PointProduct;
import kr.co.mathrank.domain.point.exception.CannotFoundPointProductException;
import kr.co.mathrank.domain.point.repository.PointProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PointProductService {
	private final PointProductRepository pointProductRepository;

	public Long save(@NotNull @Valid final PointProductCreateCommand command) {
		final PointProduct point = command.toEntity();
		final PointProduct pointProduct = pointProductRepository.save(point);
		log.info(
			"[PointProductService.save] saved point product - pointProductId: {}, pointAmount: {}, currency: {}, costAmount: {}",
			pointProduct.getId(), pointProduct.getPointAmount(), point.getCurrency(), point.getPrice());
		return pointProduct.getId();
	}

	public void delete(@NotNull @Valid final PointProductDeleteCommand command) {
		pointProductRepository.deleteById(command.pointProductId());
		log.info("[PointProductService.delete] delete point product - pointProductId: {}", command.pointProductId());
	}

	public PointProductQueryResults queryAll() {
		return new PointProductQueryResults(pointProductRepository.findAll().stream()
			.map(PointProductQueryResult::from)
			.sorted((a, b) -> a.pointAmount().compareTo(b.pointAmount())) // 오름차순
			.toList());
	}

	public PointProductQueryResult querySingle(@NotNull final Long pointProductId) {
		return pointProductRepository.findById(pointProductId)
			.map(PointProductQueryResult::from)
			.orElseThrow(() -> {
				log.info("[PointProductService.querySingle] cannot found point product - pointProductId: {}", pointProductId);
				return new CannotFoundPointProductException();
			});
	}
}
