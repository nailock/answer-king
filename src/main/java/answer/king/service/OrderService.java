package answer.king.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

	public void addItem(Long id, Long itemId, int quantity) {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);

		if (order.getItems() == null) {
			ArrayList<LineItem> items = new ArrayList<>();
			order.setItems(items);
		} else {
			Optional<LineItem> findItem = order.getItems()
								.stream()
								.filter(e -> e.getItemId().equals(itemId))
								.findFirst();
			if (findItem.isPresent()) {
				LineItem foundItem = findItem.get();
				foundItem.setQuantity(foundItem.getQuantity() + quantity);
				lineItemRepository.save(foundItem);
				return;
			}
			
		}

		LineItem lineItem = new LineItem();
		lineItem.setItemId(item.getId());
		lineItem.setCurrentPrice(item.getPrice());
		lineItem.setQuantity(quantity);
		lineItemRepository.save(lineItem);

		

		order.getItems().add(lineItem);

		orderRepository.save(order);
	}

	public Receipt pay(Long id, BigDecimal payment) {
		Order order = orderRepository.findOne(id);

		Receipt receipt = new Receipt();
		receipt.setOrder(order);

		if (order.getPaid()) {
			receipt.setPayment(BigDecimal.ZERO);
			receipt.setText("Order already paid");
			return receipt;
		}

		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(LineItem::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

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
