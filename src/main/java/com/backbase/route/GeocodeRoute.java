package com.backbase.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.backbase.processor.GeocodeProcessor;

@Component
public class GeocodeRoute extends RouteBuilder {

	@Value("${server.port}")
	String serverPort;

	@Autowired
	GeocodeProcessor geocodeProcessor;

	@Override
	public void configure() {

		CamelContext context = new DefaultCamelContext();

		restConfiguration().port(serverPort).enableCORS(true);

		rest("/api/").description("Geocode Services").id("api-route").get("/geocode")
				.produces(MediaType.APPLICATION_JSON_VALUE).consumes(MediaType.APPLICATION_JSON_VALUE)
				.to("direct:geoservice");

		from("direct:geoservice").routeId("direct-route").to(
				"https4:maps.googleapis.com/maps/api/geocode/xml?address=${header.address}&key=AIzaSyCkqDi0DBNZhNM9ieBzsY83p0uzKmKiluc&bridgeEndpoint=true&throwExceptionOnFailure=false")
				.convertBodyTo(String.class).choice().when(xpath("/GeocodeResponse/status/text() = 'OVER_QUERY_LIMIT'"))
				.setBody(constant(null)).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(429))
				.when(xpath("/GeocodeResponse/status/text() = 'ZERO_RESULTS'")).setBody(constant(null))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)).otherwise()
				.setHeader("formatted_address", xpath("/GeocodeResponse/result/formatted_address/text()"))
				.setHeader("latitude", xpath("/GeocodeResponse/result/geometry/location/lat/text()"))
				.setHeader("longitude", xpath("/GeocodeResponse/result/geometry/location/lng/text()"))
				.process(geocodeProcessor).marshal().json().end();
	}

}
