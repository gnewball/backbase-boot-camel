package com.backbase.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backbase.processor.GeocodeProcessor;

@Component
public class GeocodeRoute extends RouteBuilder {

	@Autowired
	GeocodeProcessor geocodeProcessor;

	@Override
	public void configure() {

		from("direct:geoservice").routeId("direct-route")
				.tracing().log("${in.header.address}")
				.toD("https4:maps.googleapis.com/maps/api/geocode/xml?address=${in.header.address}&key=AIzaSyCkqDi0DBNZhNM9ieBzsY83p0uzKmKiluc&bridgeEndpoint=true&throwExceptionOnFailure=false")
				.convertBodyTo(String.class)
				.log("${body}")
				.choice().when(xpath("/GeocodeResponse/status/text() = 'OVER_QUERY_LIMIT'"))
					.setBody(constant(null)).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(429))
				.when(xpath("/GeocodeResponse/status/text() = 'ZERO_RESULTS'")).setBody(constant(null))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
				.otherwise()
					.setHeader("formatted_address", xpath("/GeocodeResponse/result/formatted_address/text()"))
					.setHeader("latitude", xpath("/GeocodeResponse/result/geometry/location/lat/text()"))
					.setHeader("longitude", xpath("/GeocodeResponse/result/geometry/location/lng/text()"))
					.process(geocodeProcessor).marshal().json()
				.end();
	}

}
