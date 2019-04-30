package answer.king.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.OrderRepository;
import answer.king.repo.ReceiptRepository;;

@Service
@Transactional
public class ReceiptService {

	@Autowired
	private ReceiptRepository receiptRepository;

	@Autowired
	private OrderRepository orderRepository;

	public List<Receipt> getAll() {
		return receiptRepository.findAll();
	}

	public Receipt getReceipt(Long id) {
		Receipt receipt = receiptRepository.findOne(id);
		Hibernate.initialize(receipt.getOrder());
		return receipt;
	}

	public Receipt getReceiptByOrder(Long orderid) {
		Receipt exreceipt = new Receipt();
		Order order = new Order();
		order.setId(orderid);
		exreceipt.setOrder(order);
		Example<Receipt> ex = Example.of(exreceipt);
		return receiptRepository.findOne(ex);
	}

	public void addOrder(Long id, Long orderId) {
		Receipt receipt = receiptRepository.findOne(id);
		Order order = orderRepository.findOne(orderId);

		receipt.setOrder(order);

		receiptRepository.save(receipt);
	}

	public Receipt save(Receipt receipt) {
		return receiptRepository.save(receipt);
	}


}