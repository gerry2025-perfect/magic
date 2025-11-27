package com.iwhalecloud.bss.magic.magicapi.modules.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.MagicModule;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * http 模块
 *
 * @author mxd
 * @since 1.1.0
 */
@MagicModule("http")
public class HttpModule {

	private final RestTemplate template;
	private final HttpHeaders httpHeaders = new HttpHeaders();
	private Class<?> responseType = Object.class;
	private final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
	private final MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
	private final Map<String, ?> variables = new HashMap<>();
	private String url;
	private HttpMethod method = HttpMethod.GET;
	private HttpEntity<Object> entity = null;
	private Object requestBody;

	public HttpModule(RestTemplate template) {
		this.template = template;
	}

	public HttpModule(RestTemplate template, String url) {
		this.template = template;
		this.url = url;
	}

	@Comment("Create connection")
	public HttpModule connect(@Comment(name = "url", value = "Target URL") String url) {
		return new HttpModule(template, url);
	}

	@Comment("Set URL parameters")
	public HttpModule param(@Comment(name = "key", value = "Parameter name") String key,
							@Comment(name = "values", value = "Parameter value") Object... values) {
		if (values != null) {
			for (Object value : values) {
				this.params.add(key, value);
			}
		}
		return this;
	}

	@Comment("Batch set URL parameters")
	public HttpModule param(@Comment(name = "values", value = "Parameter value") Map<String, Object> values) {
		values.forEach((key, value) -> param(key, Objects.toString(value, "")));
		return this;
	}

	@Comment("Set form parameters")
	public HttpModule data(@Comment(name = "key", value = "Parameter name") String key,
						   @Comment(name = "values", value = "Parameter value") Object... values) {
		if (values != null) {
			for (Object value : values) {
				this.data.add(key, value);
			}
		}
		return this;
	}

	@Comment("Batch set form parameters")
	public HttpModule data(@Comment(name = "values", value = "Parameter value") Map<String, Object> values) {
		values.forEach((key, value) -> data(key, Objects.toString(value, "")));
		return this;
	}

	@Comment("Set header")
	public HttpModule header(@Comment(name = "key", value = "Header name") String key,
							 @Comment(name = "value", value = "Header value") String value) {
		httpHeaders.add(key, value);
		return this;
	}

	@Comment("Batch set header")
	public HttpModule header(@Comment(name = "values", value = "Header value") Map<String, Object> values) {
		values.entrySet()
				.stream()
				.filter(it -> it.getValue() != null)
				.forEach(entry -> header(entry.getKey(), entry.getValue().toString()));
		return this;
	}

	@Comment("Set request method, default GET")
	public HttpModule method(@Comment(name = "method", value = "Request method") HttpMethod method) {
		this.method = method;
		return this;
	}

	@Comment("Set `RequestBody`")
	public HttpModule body(@Comment(name = "requestBody", value = "`RequestBody`") Object requestBody) {
		this.requestBody = requestBody;
		this.contentType(MediaType.APPLICATION_JSON);
		return this;
	}

	@Comment("Custom `HttpEntity`")
	public HttpModule entity(@Comment(name = "entity", value = "`HttpEntity`") HttpEntity<Object> entity) {
		this.entity = entity;
		return this;
	}

	@Comment("Set `ContentType`")
	public HttpModule contentType(@Comment(name = "contentType", value = "Content-Type value") String contentType) {
		return contentType(MediaType.parseMediaType(contentType));
	}

	@Comment("Set `ContentType`")
	public HttpModule contentType(@Comment(name = "mediaType", value = "Content-Type value") MediaType mediaType) {
		this.httpHeaders.setContentType(mediaType);
		return this;
	}

	@Comment("Sets the return value to `byte[]`")
	public HttpModule expectBytes() {
		this.responseType = byte[].class;
		return this;
	}

	@Comment("Sets the return value to `String`")
	public HttpModule expectString() {
		this.responseType = String.class;
		return this;
	}

	@Comment("Sends a `POST` request")
	public ResponseEntity<?> post() {
		this.method(HttpMethod.POST);
		return this.execute();
	}

	@Comment("Sends a `GET` request")
	public ResponseEntity<?> get() {
		this.method(HttpMethod.GET);
		return this.execute();
	}

	@Comment("Sends a `PUT` request")
	public ResponseEntity<?> put() {
		this.method(HttpMethod.PUT);
		return this.execute();
	}

	@Comment("Sends a `DELETE` request")
	public ResponseEntity<?> delete() {
		this.method(HttpMethod.DELETE);
		return this.execute();
	}

	@Comment("Sends a `HEAD` request")
	public ResponseEntity<?> head() {
		this.method(HttpMethod.HEAD);
		return this.execute();
	}

	@Comment("Sends an `OPTIONS` request")
	public ResponseEntity<?> options() {
		this.method(HttpMethod.OPTIONS);
		return this.execute();
	}

	@Comment("Sends a `TRACE` request")
	public ResponseEntity<?> trace() {
		this.method(HttpMethod.TRACE);
		return this.execute();
	}

	@Comment("Sends a `PATCH` request")
	public ResponseEntity<?> patch() {
		this.method(HttpMethod.PATCH);
		return this.execute();
	}

	@Comment("Execute Request")
	public ResponseEntity<?> execute() {
		if (!this.params.isEmpty()) {
			String queryString = this.params.entrySet().stream()
					.map(it -> it.getValue().stream()
							.map(value -> it.getKey() + "=" + value)
							.collect(Collectors.joining("&"))
					).collect(Collectors.joining("&"));
			if (StringUtils.isNotBlank(queryString)) {
				this.url += (this.url.contains("?") ? "&" : "?") + queryString;
			}
		}
		if (!this.data.isEmpty()) {
			this.entity = new HttpEntity<>(this.data, this.httpHeaders);
		} else if (this.entity == null && this.requestBody != null) {
			this.entity = new HttpEntity<>(this.requestBody, this.httpHeaders);
		} else {
			this.entity = new HttpEntity<>(null, this.httpHeaders);
		}
		return template.exchange(url, this.method, entity, responseType, variables);
	}
}
