package com.iwhalecloud.bss.magic.magicapi.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mongo FindIterable 方法扩展
 *
 * @author mxd
 */
public class MongoFindIterableExtension {

	@Comment("Convert results to List")
	public List<Map<String, Object>> list(FindIterable<Document> iterable) {
		MongoCursor<Document> cursor = iterable.iterator();
		List<Map<String, Object>> result = new ArrayList<>();
		while (cursor.hasNext()) {
			result.add(cursor.next());
		}
		return result;
	}

}
