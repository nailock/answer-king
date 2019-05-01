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

	// Access to the receipt repo
	@Autowired
	private ReceiptRepository receiptRepository;

	// Access to the lineitem repo
	@Autowired
	private LineItemRepository lineItemRepository;

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	// Find a single order
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
			// If there's no item array, set one up
			ArrayList<LineItem> items = new ArrayList<>();
			order.setItems(items);
		} else {
			// First, check to see if this lineitem exists on the order
			Optional<LineItem> findItem = order.getItems()
								.stream()
								.filter(e -> e.getItemId().equals(itemId))
								.findFirst();
			if (findItem.isPresent()) {
				// If it does, just increase the quantity of the existing lineitem
				LineItem foundItem = findItem.get();
				foundItem.setQuantity(foundItem.getQuantity() + quantity);
				lineItemRepository.save(foundItem);
				return;
			}
			
		}

		// If we got here, it must be a new lineitem
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
			// We shouldn't allow an order to be paid twice
			receipt.setPayment(BigDecimal.ZERO);
			receipt.setText("Order already paid");
			return receipt;
		}

		// Figure out the total price of the order
		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(LineItem::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Decline payment if funds are insufficient
		if (payment.compareTo(totalOrderPrice) == -1) {
			receipt.setPayment(BigDecimal.ZERO);
			receipt.setText("Insufficient funds");
		} else {
			// happy
			receipt.setPayment(payment);
			receipt.setText("Approved");
			order.setPaid(true);
			orderRepository.save(order);
			receiptRepository.save(receipt);
		}
		
		return receipt;
	}
}
