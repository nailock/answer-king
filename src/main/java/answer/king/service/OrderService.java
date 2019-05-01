package answer.king.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.LineItemRepository;
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

	@Autowired
	private LineItemRepository lineItemRepository;

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

		LineItem lineItem = new LineItem();
		lineItem.setItemId(item.getId());
		lineItem.setCurrentPrice(item.getPrice());
		lineItem.setQuantity(1);
		lineItemRepository.save(lineItem);

		if (order.getItems() == null) {
			ArrayList<LineItem> items = new ArrayList<>();
			order.setItems(items);
		}

		order.getItems().add(lineItem);

		orderRepository.save(order);
	}

	public Receipt pay(Long id, BigDecimal payment) {
		Order order = orderRepository.findOne(id);

		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(LineItem::getCurrentPrice)
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
