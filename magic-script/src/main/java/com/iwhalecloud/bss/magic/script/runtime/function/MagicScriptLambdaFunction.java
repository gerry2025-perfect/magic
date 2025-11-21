package com.iwhalecloud.bss.magic.script.runtime.function;

import com.iwhalecloud.bss.magic.script.runtime.Variables;

@FunctionalInterface
public interface MagicScriptLambdaFunction {

	Object apply(Variables variables, Object[] args);
}
