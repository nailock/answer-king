package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.repo.ReceiptRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ReceiptRepository receiptRepository;

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	public Order getOrder(Long id) {
		Order order = orderRepository.findOne(id);
		Hibernate.initialize(order.getItems());
		return order;
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

	public void addItem(Long id, Long itemId) {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);

		item.setOrder(order);
		order.getItems().add(item);

		orderRepository.save(order);
	}

	public Receipt pay(Long id, BigDecimal payment) {
		Order order = orderRepository.findOne(id);

		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(Item::getPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		Receipt receipt = new Receipt();
		receipt.setOrder(order);

		if (payment.compareTo(totalOrderPrice) == -1) {
			receipt.setPayment(BigDecimal.ZERO);
			receipt.setText("Insufficient funds");
		} else {
			receipt.setPayment(payment);
			receipt.setText("Approved");
			order.setPaid(true);
			orderRepository.save(order);
			receiptRepository.save(receipt);
		}
		
		return receipt;
	}
}
