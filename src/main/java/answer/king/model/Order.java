package answer.king.model;

import answer.king.model.LineItem;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.Proxy;

import lombok.Data;

@Entity
@Table(name = "T_ORDER")
@Data // Lombok can generate our getters and setters
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Boolean paid = false;

	// We have lineitems on the order now instead
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL, CascadeType.PERSIST })
	@JoinColumn(name = "LINEITEM_ID")
	private List<LineItem> items;

	// Serialisation function
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
