package answer.king.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Receipt {

	private BigDecimal payment;

	private Order order;

	public BigDecimal getChange() {
		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(Item::getPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return payment.subtract(totalOrderPrice);
	}
}
