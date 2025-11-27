package com.iwhalecloud.bss.magic.magicapi.mongo;

import org.bson.types.ObjectId;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicFunction;
import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;

import java.util.Date;

public class MongoFunction implements MagicFunction {

	@Comment("Create ObjectId")
	@Function
	public ObjectId ObjectId(String hexString){
		return new ObjectId(hexString);
	}

	@Comment("Create ObjectId")
	@Function
	public ObjectId ObjectId(){
		return new ObjectId();
	}

	@Comment("Create ObjectId")
	@Function
	public ObjectId ObjectId(byte[] bytes){
		return new ObjectId(bytes);
	}

	@Comment("Create ObjectId")
	@Function
	public ObjectId ObjectId(Date date){
		return new ObjectId(date);
	}
}
