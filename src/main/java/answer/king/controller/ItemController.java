package answer.king.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Item;
import answer.king.service.ItemService;

@RestController
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Item> getAll() {
		return itemService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestBody Item item) {
		try {
			item = itemService.save(item);
			String itemJson = item.toJson();
			return new ResponseEntity<>(itemJson, HttpStatus.OK);
		} catch (JsonProcessingException ex) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
