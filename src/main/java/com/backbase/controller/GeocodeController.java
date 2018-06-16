package com.backbase.controller;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeocodeController {

	@Autowired
	private FluentProducerTemplate fluentProducerTemplate;

	@GetMapping(path="/backbase/geocode")
	public ResponseEntity getGeocodeCoordinates(@RequestParam String address) {
		
		Exchange result = fluentProducerTemplate
			    .withHeader("address", address)
			    .to("direct:geoservice").send();
		
		return ResponseEntity
				.status(Integer.parseInt(result.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()))
				.body(result.getIn().getBody());
	}

}
