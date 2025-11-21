package com.iwhalecloud.bss.magic.script.functions;

import java.util.List;

public interface DynamicMethod {

	Object execute(String methodName, List<Object> parameters);

}