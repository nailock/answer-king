package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.LineItem;

import answer.king.repo.LineItemRepository;


@Service
@Transactional
public class LineItemService {

	@Autowired
	private LineItemRepository lineItemRepository;

	public List<LineItem> getAll() {
		return lineItemRepository.findAll();
	}

	public LineItem getItem(Long id) {
		LineItem item = lineItemRepository.findOne(id);
		return item;
	}

	public LineItem save(LineItem item) {
		return lineItemRepository.save(item);
	}
}
