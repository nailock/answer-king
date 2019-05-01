package answer.king.model;

import answer.king.model.Order;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "T_RECEIPT")
@Data // Lombok can generate our getters and setters
public class Receipt {

	// Give this an ID since we're making it an entity
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private BigDecimal payment;

	// This should just refer to the order table
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID")
	private Order order;

	// Receipt text will help the customer see what the problem was
	private String text;

	// We don't want to persist the change value
	@JsonProperty(access = Access.READ_ONLY)
	private BigDecimal change;

	public BigDecimal getChange() {
		// Safely calculate change
		if (order == null ||
			payment == null ||
			payment.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal totalOrderPrice = order.getItems()
			.stream()
			.map(LineItem::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return payment.subtract(totalOrderPrice);
	}

	// Serialisation function
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
