package answer.king.controller;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Order> getAll() {
		return orderService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public Order create() {
		return orderService.save(new Order());
	}

	// Returning response entities allows us to provide detailed error information
	// This function now allows for an item quantity to be specified
	@RequestMapping(value = "/{id}/addItem/{itemId}/quantity/{quantity}", method = RequestMethod.PUT)
	public ResponseEntity<String> addItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId, @PathVariable int quantity) {
		// Sanitise
		if (quantity < 0) {
			return new ResponseEntity<>("{ \"error\": \"Quantity cannot be negative.\" }", HttpStatus.BAD_REQUEST);
		}
		orderService.addItem(id, itemId, quantity);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
	public ResponseEntity<String> pay(@PathVariable("id") Long id, @RequestBody BigDecimal payment) {
		// Sanitise
		if (payment.signum() == -1) {
			return new ResponseEntity<>("{ \"error\": \"Payment cannot be negative.\" }", HttpStatus.BAD_REQUEST);
		}
		
		Receipt receipt = orderService.pay(id, payment);
		String receiptJson = "";
		try {
			receiptJson = receipt.toJson();
		} catch (JsonProcessingException ex) {
			String error = "{ \"error\": \"Exception serialising receipt to JSON.\" }";
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(receiptJson, HttpStatus.OK);
	}

}
