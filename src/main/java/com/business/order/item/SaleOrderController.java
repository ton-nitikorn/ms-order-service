package com.business.order.item;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/order")
public class SaleOrderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SaleOrderController.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@HystrixCommand(fallbackMethod = "getPromotionFallback")
	@GetMapping("/setPromotion/{id}/{promotionCode}")
	public Map<String, String> findOrderById(
			@PathVariable Long id,
			@PathVariable String promotionCode
	){
		LOGGER.info("call promotion service");
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("id", id.toString());
		result.put("promotion",promotionCode);
		result.put("subnet","330");
		
		@SuppressWarnings("unchecked")
		Map<String, String> promotion = restTemplate.getForObject("http://promotion-service/promotion/"+promotionCode, Map.class);
		
		result.put("discount", promotion.get("discount"));
		result.put("net", String.valueOf(Double.valueOf("330") - Double.valueOf(promotion.get("discount"))));
		LOGGER.info("response from promotion");
		return result;
	}
	
	public Map<String, String> getPromotionFallback(Long id, String promotionCode, Throwable hystrixCommand){
		Map<String, String> result = new HashMap<String, String>();
		System.out.println(hystrixCommand.getMessage());
		result.put("success", "false");
		result.put("messaage", hystrixCommand.getMessage());
		return result;
	}

}
