package com.backbase.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.backbase.model.GeocodeResponse;

@Component
public class GeocodeProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		GeocodeResponse geocodeResponse = new GeocodeResponse();
		Message message = exchange.getIn();
		geocodeResponse.setFormatted_address(message.getHeader("formatted_address", String.class));
		geocodeResponse.setLatitude(message.getHeader("latitude", String.class));
		geocodeResponse.setLongitude(message.getHeader("longitude", String.class));
		exchange.getIn().setBody(geocodeResponse);

	}
}
