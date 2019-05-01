package answer.king.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Entity
@Table(name = "T_LINEITEM")
@Data // Lombok can generate our getters and setters
public class LineItem {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int quantity;

    private BigDecimal currentPrice;

    private Long itemId;

    // If we have multiples of the same LineItem make sure we calculate the
    // total price accordingly
    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return currentPrice.multiply(new BigDecimal(quantity));
    }

    // Serialisation function
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
     }
}