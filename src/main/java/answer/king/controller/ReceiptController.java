package answer.king.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;
import answer.king.service.ReceiptService;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {
	// API to handle the receipts

	@Autowired
	private ReceiptService receiptService;

	// fetch all the receipts
	@RequestMapping(method = RequestMethod.GET)
	public List<Receipt> getAll() {
		return receiptService.getAll();
	}

	// get one by id
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Receipt getReceipt(@PathVariable("id") Long id) {
		return receiptService.getReceipt(id);
	}

	// get one using it's order id
	@RequestMapping(value = "/byOrder/{orderId}", method = RequestMethod.GET)
	public Receipt getReceiptByOrder(@PathVariable("orderId") Long orderId) {
		return receiptService.getReceiptByOrder(orderId);
	}
	

	// Persist a receipt
	@RequestMapping(method = RequestMethod.POST)
	public Receipt saveReceipt(@RequestBody Receipt receipt) {
		return receiptService.save(receipt);
	}
	
	// Set a receipt's order
	@RequestMapping(value = "/{id}/addOrder/{orderId}", method = RequestMethod.PUT)
	public void addOrder(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId) {
		receiptService.addOrder(id, orderId);
	}

}