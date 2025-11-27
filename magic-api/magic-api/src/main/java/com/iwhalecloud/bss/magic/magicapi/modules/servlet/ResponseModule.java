package com.iwhalecloud.bss.magic.magicapi.modules.servlet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.MagicModule;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestContext;
import com.iwhalecloud.bss.magic.magicapi.core.interceptor.ResultProvider;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletResponse;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * response模块
 *
 * @author mxd
 */
@MagicModule("response")
public class ResponseModule {

	private final ResultProvider resultProvider;

	public ResponseModule(ResultProvider resultProvider) {
		this.resultProvider = resultProvider;
	}

	/**
	 * 文件下载
	 *
	 * @param value    文件内容
	 * @param filename 文件名
	 */
	@Comment("File Download")
	public static ResponseEntity<?> download(@Comment(name = "value", value = "File Content, such as `byte[]`") Object value,
											 @Comment(name = "filename", value = "File Name") String filename) throws UnsupportedEncodingException {
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"))
				.body(value);
	}

	/**
	 * 自行构建分页结果
	 *
	 * @param total  条数
	 * @param values 数据内容
	 */
	@Comment("Return Custom Pagination Results")
	public Object page(@Comment(name = "total", value = "Total number of rows") long total,
					   @Comment(name = "values", value = "Current result set") List<Map<String, Object>> values) {
		return resultProvider.buildPageResult(RequestContext.getRequestEntity(), null, total, values);
	}

	/**
	 * 自定义json结果
	 *
	 * @param value json内容
	 */
	@Comment("Custom returned JSON content")
	public ResponseEntity<Object> json(@Comment(name = "value", value = "Return object") Object value) {
		return ResponseEntity.ok(value);
	}

	/**
	 * 添加Header
	 */
	@Comment("Add response header")
	public ResponseModule addHeader(@Comment(name = "key", value = "Header name") String key,
									@Comment(name = "value", value = "Header value") String value) {
		if (StringUtils.isNotBlank(key)) {
			MagicHttpServletResponse response = getResponse();
			if (response != null) {
				response.addHeader(key, value);
			}
		}
		return this;
	}

	/**
	 * 设置header
	 */
	@Comment("Set response header")
	public ResponseModule setHeader(@Comment(name = "key", value = "Header name") String key,
									@Comment(name = "value", value = "Header value") String value) {
		if (StringUtils.isNotBlank(key)) {
			MagicHttpServletResponse response = getResponse();
			if (response != null) {
				response.setHeader(key, value);
			}
		}
		return this;
	}

	/**
	 * 获取OutputStream
	 *
	 * @since 1.2.3
	 */
	@Comment("Get OutputStream")
	public OutputStream getOutputStream() throws IOException {
		MagicHttpServletResponse response = getResponse();
		return response.getOutputStream();
	}


	@Comment("Terminate output; this method will not perform any output or processing on the result")
	public NullValue end() {
		return NullValue.INSTANCE;
	}

	private MagicHttpServletResponse getResponse() {
		return RequestContext.getHttpServletResponse();
	}

	/**
	 * 展示图片
	 *
	 * @param value 图片内容
	 * @param mime  图片类型，image/png,image/jpeg,image/gif
	 */
	@Comment("Output image")
	public ResponseEntity image(@Comment(name = "value", value = "Image content, such as `byte[]`") Object value,
								@Comment(name = "mime", value = "Image type, such as `image/png`, `image/jpeg`, `image/gif`") String mime) {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, mime).body(value);
	}

	/**
	 * 输出文本
	 *
	 * @param text 文本内容
	 */
	@Comment("Output Text")
	public ResponseEntity text(@Comment(name = "text", value = "Text Content") String text) {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE).body(text);
	}

	/**
	 * 重定向
	 *
	 * @param url 目标网址
	 */
	@Comment("Redirection")
	public NullValue redirect(@Comment(name = "url", value = "Target URL") String url) throws IOException {
		getResponse().sendRedirect(url);
		return NullValue.INSTANCE;
	}

	public static class NullValue {
		static final NullValue INSTANCE = new NullValue();
	}
}
