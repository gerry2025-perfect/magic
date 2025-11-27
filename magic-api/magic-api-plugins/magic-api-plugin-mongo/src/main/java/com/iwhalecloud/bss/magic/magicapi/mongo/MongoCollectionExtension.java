package com.iwhalecloud.bss.magic.magicapi.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MongoCollection方法扩展
 *
 * @author mxd
 */
public class MongoCollectionExtension {

	@Comment("Perform batch insert operation")
	public void insert(MongoCollection<Document> collection,
					   @Comment(name = "maps", value = "Collection to insert") List<Map<String, Object>> maps) {
		collection.insertMany(maps.stream().map(Document::new).collect(Collectors.toList()));
	}

	@Comment("Perform single insert operation")
	public void insert(MongoCollection<Document> collection,
					   @Comment(name = "map", value = "Perform insert data") Map<String, Object> map) {
		insert(collection, Collections.singletonList(map));
	}

	@Comment("Perform query operation")
	public FindIterable<Document> find(MongoCollection<Document> collection,
									   @Comment(name = "query", value = "Query conditions") Map<String, Object> query) {
		return collection.find(new Document(query));
	}

	@Comment("Modification operation, returns the number of modifications")
	public long update(MongoCollection<Document> collection,
					   @Comment(name = "query", value = "Query conditions") Map<String, Object> query,
					   @Comment(name = "update", value = "Modified values") Map<String, Object> update) {
		return collection.updateOne(new Document(query), new Document(update)).getModifiedCount();
	}

	@Comment("Batch edits, returns the number of edits")
	public long updateMany(MongoCollection<Document> collection,
						   @Comment(name = "query", value = "Editing criteria") Map<String, Object> query,
						   @Comment(name = "update", value = "Modified values") Map<String, Object> update) {
		return collection.updateMany(new Document(query), new Document(update)).getModifiedCount();
	}

	@Comment("Batch edits, returns the number of edits")
	public long updateMany(MongoCollection<Document> collection,
						   @Comment(name = "query", value = "Query conditions") Map<String, Object> query,
						   @Comment(name = "update", value = "Modified values") Map<String, Object> update,
						   @Comment(name = "filters", value = "Filtering criteria") Map<String, Object> filters) {
		UpdateOptions updateOptions = new UpdateOptions();
		if (filters != null && !filters.isEmpty()) {
			Object upsert = filters.get("upsert");
			if (upsert != null) {
				filters.remove("upsert");
				updateOptions.upsert(Boolean.parseBoolean(upsert.toString()));
			}
			Object bypassDocumentValidation = filters.get("bypassDocumentValidation");
			if (bypassDocumentValidation != null) {
				filters.remove("bypassDocumentValidation");
				updateOptions.bypassDocumentValidation(Boolean.parseBoolean(bypassDocumentValidation.toString()));
			}
			List<Document> arrayFilters = filters.entrySet().stream().map(entry -> new Document(entry.getKey(), entry.getValue())).collect(Collectors.toList());
			updateOptions.arrayFilters(arrayFilters);
		}
		return collection.updateMany(new Document(query), new Document(update), updateOptions).getModifiedCount();
	}

	@Comment("Number of queries")
	public long count(MongoCollection<Document> collection,
					  @Comment(name = "query", value = "Query") Map<String, Object> query) {
		return collection.countDocuments(new Document(query));
	}

	@Comment("Batch deletion, returns the number of deleted records")
	public long remove(MongoCollection<Document> collection,
					   @Comment(name = "query", value = "Delete criteria") Map<String, Object> query) {
		return collection.deleteMany(new Document(query)).getDeletedCount();
	}

	@Comment("Delete one record, returns the number of deleted records")
	public long removeOne(MongoCollection<Document> collection,
						  @Comment(name = "query", value = "Delete criteria") Map<String, Object> query) {
		return collection.deleteOne(new Document(query)).getDeletedCount();
	}
}
