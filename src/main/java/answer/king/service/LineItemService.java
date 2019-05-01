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
    // New service to manipulate LineItems

    // Access to the repo
	@Autowired
	private LineItemRepository lineItemRepository;

    // Fetch all the LineItems - maybe we don't need this
	public List<LineItem> getAll() {
		return lineItemRepository.findAll();
	}

    // Fetch a lineitem
	public LineItem getItem(Long id) {
		LineItem item = lineItemRepository.findOne(id);
		return item;
	}

    // Persist a lineitem
	public LineItem save(LineItem item) {
		return lineItemRepository.save(item);
	}
}
