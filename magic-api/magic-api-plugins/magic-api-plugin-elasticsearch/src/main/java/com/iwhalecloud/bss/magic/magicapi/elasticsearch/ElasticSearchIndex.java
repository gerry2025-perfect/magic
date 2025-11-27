package com.iwhalecloud.bss.magic.magicapi.elasticsearch;

import org.elasticsearch.client.RestClient;
import com.iwhalecloud.bss.magic.magicapi.utils.JsonUtils;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ElasticSearchIndex {

	private final RestClient restClient;

	private final String name;

	private final String type;


	public ElasticSearchIndex(RestClient restClient, String name, String type) {
		this.restClient = restClient;
		this.name = name;
		this.type = type;
	}

	@Comment("Save based on `_id`, update if it exists, insert if it does not exist")
	public Object save(@Comment(value = "_id", name = "_id")String _id, @Comment(value = "Save object", name = "data")Object data) throws IOException {
		return connect("/%s/%s/%s", this.name, this.type, _id).post(data);
	}

	@Comment("Insert without specifying `_id`")
	public Object insert(@Comment(value = "Insert object", name = "data")Object data) throws IOException {
		return connect("/%s/%s", this.name, this.type).post(data);
	}

	@Comment("Insert by specifying `_id`; will not update if `_id` already exists")
	public Object insert(@Comment(value = "_id", name = "_id")String _id, @Comment(value = "Insert object", name = "data")Object data) throws IOException {
		return connect("/%s/%s/%s/_create", this.name, this.type, _id).post(data);
	}

	@Comment("Delete by `id`")
	public Object delete(@Comment(value = "id", name = "id")String id) throws IOException {
		return connect("/%s/%s/%s", this.name, this.type, id).delete();
	}

	@Comment("Batch save; when `id` is included, this column value will be used to match and save")
	public Object bulkSave(@Comment(value = "Saved content", name = "list") List<Map<String, Object>> list) throws IOException {
		StringBuilder builder = new StringBuilder();
		list.forEach(item -> {
			Object id = item.get("id");
			if(id != null){
				builder.append(String.format("{ \"index\":{ \"_id\": \"%s\" } }\r\n", id));
			} else {
				builder.append("{ \"index\":{} }\r\n");
			}
			builder.append(JsonUtils.toJsonStringWithoutPretty(item));
			builder.append("\r\n");
		});
		return connect("/%s/%s/_bulk", this.name, this.type).post(builder.toString());
	}

	@Comment("Modify by `_id`")
	public Object update(@Comment(value = "_id", name = "_id")String _id, @Comment(value = "Modified items", name = "data")Object data) throws IOException {
		return connect("/%s/%s/%s", this.name, this.type, _id).post(Collections.singletonMap("doc", data));
	}

	@Comment("Search")
	public Object search(@Comment(value = "Search for `DSL` statements", name = "dsl")Map<String, Object> dsl) throws IOException {
		return connect("/%s/_search", this.name).post(dsl);
	}

	private ElasticSearchConnection connect(String format, Object... args) {
		return new ElasticSearchConnection(this.restClient, String.format(format, args));
	}
}
