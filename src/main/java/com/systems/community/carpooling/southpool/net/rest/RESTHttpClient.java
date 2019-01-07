package com.systems.community.carpooling.southpool.net.rest;

import org.springframework.web.client.RestTemplate;

public interface RESTHttpClient {

	public RestTemplate getDefaultRestTemplate();
}
