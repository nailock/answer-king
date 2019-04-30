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

import answer.king.model.Item;
import answer.king.service.ItemService;

@RestController
@RequestMapping("/item")
public class ItemController {

	private static final String acceptableNameRegex = "^[A-Za-z0-9 _]*[A-Za-z0-9][A-Za-z0-9 _]*$";

	@Autowired
	private ItemService itemService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Item> getAll() {
		return itemService.getAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Item getItem(@PathVariable("id") Long id) {
		return itemService.getItem(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestBody Item item) {
		String valid = checkValidity(item);
		if (!valid.equals("ok")) {
			String error = "{ \"error\": \"" + valid + "\" }";
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		item = itemService.save(item);
		String itemJson = "";
		try {
			itemJson = item.toJson();
		} catch (JsonProcessingException ex) {
			String error = "{ \"error\": \"Exception serialising item to JSON.\" }";
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(itemJson, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/updatePrice/{newPrice}", method = RequestMethod.PUT)
	public ResponseEntity<String> updatePrice(@PathVariable("id") Long id, @PathVariable("newPrice") BigDecimal newPrice) {
		if (newPrice.signum() == -1) {
			return new ResponseEntity<>("{ \"error\": \"Price cannot be negative.\" }", HttpStatus.BAD_REQUEST);
		}
		itemService.updatePrice(id, newPrice);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String checkValidity(Item item) {
		if (item.getPrice().signum() == -1) {
			return "Price cannot be negative.";
		}

		if (!item.getName().matches(acceptableNameRegex)) {
			return "Only letters, numbers and spaces are accepted in the item name.";
		}

		return ("ok");
	}
}
