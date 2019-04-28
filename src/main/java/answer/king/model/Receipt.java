package answer.king.model;

import java.math.BigDecimal;

import lombok.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
public class Receipt {

	private BigDecimal payment;

	private Order order;

	private BigDecimal change;

	private String text;

	public BigDecimal getChange() {
		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(Item::getPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return payment.subtract(totalOrderPrice);
	}

	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
