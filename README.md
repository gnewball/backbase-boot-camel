Backbase Back-end Test

to start up, run: mvn spring-boot:run

then, do a GET http request to: http://localhost:8080/camel/api/geocode?address=ADDRESS

i.e http://localhost:8080/camel/api/geocode?address=Castellumweg+11

RESPONSE

{
    "com.backbase.model.GeocodeResponse": {
        "formatted__address": "Castellumweg 11, 2314 TZ Leiden, Netherlands",
        "latitude": "52.1510630",
        "longitude": 4.5192087
    }
}


Notes:

Make sure you are pointing to JDK 8