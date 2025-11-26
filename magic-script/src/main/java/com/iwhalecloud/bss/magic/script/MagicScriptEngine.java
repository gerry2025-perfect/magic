package com.iwhalecloud.bss.magic.script;

import com.iwhalecloud.bss.magic.script.ScriptClass.ScriptAttribute;
import com.iwhalecloud.bss.magic.script.ScriptClass.ScriptMethod;
import com.iwhalecloud.bss.magic.script.reflection.JavaReflection;

import javax.script.*;
import java.beans.Transient;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MagicScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable {

	private static final Map<String, Object> DEFAULT_IMPORTS = new ConcurrentHashMap<>();

	private static Map<String, ScriptClass> classMap = null;

	private final MagicScriptEngineFactory magicScriptEngineFactory;

	public MagicScriptEngine(MagicScriptEngineFactory magicScriptEngineFactory) {
		this.magicScriptEngineFactory = magicScriptEngineFactory;
	}

	public static void addScriptClass(Class<?> clazz) {
		if (classMap == null) {
			getScriptClassMap();
		}
		getScriptClass(clazz).forEach(scriptClass -> classMap.put(scriptClass.getClassName(), scriptClass));
	}

	public synchronized static Map<String, ScriptClass> getScriptClassMap() {
		if (classMap == null) {
			classMap = new HashMap<>();
			Arrays.asList(String.class, Object.class, Date.class, Integer.class, Double.class, Float.class, Long.class, List.class, Short.class, Byte.class, Boolean.class, BigDecimal.class)
					.forEach(clazz -> getScriptClass(clazz).forEach(scriptClass -> classMap.put(scriptClass.getClassName(), scriptClass)));
		}
		return classMap;
	}

	public static List<ScriptMethod> getFunctions() {
		return JavaReflection.getFunctions().stream().map(it -> new ScriptMethod(it.getExecutable())).collect(Collectors.toList());
	}

	public static Map<String, ScriptClass> getExtensionScriptClass() {
		Map<Class<?>, List<Class<?>>> extensionMap = JavaReflection.getExtensionMap();
		Map<String, ScriptClass> classMap = new HashMap<>();
		for (Map.Entry<Class<?>, List<Class<?>>> entry : extensionMap.entrySet()) {
			ScriptClass clazz = classMap.get(entry.getKey().getName());
			if (clazz == null) {
				clazz = new ScriptClass();
				classMap.put(entry.getKey().getName(), clazz);
			}
			for (Class<?> extensionClass : entry.getValue()) {
				for (ScriptMethod method : getMethod(extensionClass)) {
					clazz.addMethod(method);
				}
			}
		}
		return classMap;
	}

	public static ScriptClass getScriptClassFromClass(Class<?> clazz) {
		Class<?> superClass = clazz.getSuperclass();
		ScriptClass scriptClass = new ScriptClass();
		scriptClass.setClassName(clazz.getName());
		scriptClass.setSuperClass(superClass != null ? superClass.getName() : null);
		appendMethod(clazz, scriptClass);
		return scriptClass;
	}

	public static List<ScriptClass> getScriptClass(Class<?> clazz) {
		List<ScriptClass> classList = new ArrayList<>();
		Class<?> superClass;
		do {
			superClass = clazz.getSuperclass();
			ScriptClass scriptClass = new ScriptClass();
			scriptClass.setClassName(clazz.getName());
			scriptClass.setSuperClass(superClass != null ? superClass.getName() : null);
			if (clazz.isEnum()) {
				scriptClass.setEnums(clazz.getEnumConstants());
			} else {
				appendAttributes(clazz, scriptClass);
			}
			appendMethod(clazz, scriptClass);
			classList.add(scriptClass);
			Class<?>[] interfaces = clazz.getInterfaces();
			List<String> interfaceList = new ArrayList<>();
			for (Class<?> interfaceClazz : interfaces) {
				classList.addAll(getScriptClass(interfaceClazz));
				interfaceList.add(interfaceClazz.getName());
			}
			scriptClass.setInterfaces(interfaceList);
			clazz = superClass;
		} while (superClass != null && superClass != Object.class && superClass != Class.class);
		return classList;
	}

	private static void appendMethod(Class<?> clazz, ScriptClass scriptClass) {
		//对于被代理类的类，需求获取对应实际类，然后获取
		List<ScriptMethod> methodList ;
		if(clazz.getSuperclass()!=null && clazz.getSuperclass().getName().startsWith("com.iwhalecloud.bss")) {
			methodList = getMethod(clazz.getSuperclass());
		}else{
			methodList = getMethod(clazz);
		}
		methodList.forEach(method -> {
			scriptClass.addMethod(method);
			String methodName = method.getName();
			if (method.getParameters().isEmpty() && ((methodName.startsWith("get") && methodName.length() > 3) || (methodName.startsWith("is") && methodName.length() > 2))) {
				String attributeName = method.getName().substring(methodName.startsWith("get") ? 3 : 2);
				attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
				if (!"class".equalsIgnoreCase(attributeName)) {
					scriptClass.addAttribute(new ScriptAttribute(method.getReturnType(), attributeName));
				}
			}
		});
	}

	public static Set<ScriptClass> getScriptClass(String className) {
		try {
			return new LinkedHashSet<>(getScriptClass(MagicResourceLoader.forName(className)));
		} catch (ClassNotFoundException e) {
			return Collections.emptySet();
		}
	}

	private static List<ScriptMethod> getMethod(Class<?> clazz) {
		return getMethod(clazz, null);
	}

	private static List<ScriptMethod> getMethod(Class<?> clazz, List<ScriptMethod> methods) {
		if(methods==null ){
			methods = new ArrayList<>();
		}
		try {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (Method declaredMethod : declaredMethods) {
				if (!Modifier.isVolatile(declaredMethod.getModifiers())) {
					if (Modifier.isPublic(declaredMethod.getModifiers()) && declaredMethod.getAnnotation(Transient.class) == null) {
						if (Modifier.isPublic(declaredMethod.getModifiers())) {
							methods.add(new ScriptMethod(declaredMethod));
						}
					}
				}
			}
			//对于存在父级Module的情况需要把父级的方法也补充进去
			Annotation[] annotations = clazz.getSuperclass().getAnnotations();
			if(annotations!=null && annotations.length>0) {
				for (Annotation annotation : annotations) {
					if("MagicModule".equals(annotation.annotationType().getSimpleName())){
						getMethod(clazz.getSuperclass(), methods);
					}
				}

			}
		} catch (Exception ignored) {
		}
		return methods;
	}

	private static void appendAttributes(Class<?> clazz, ScriptClass target) {
		try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isPublic(field.getModifiers()) && field.getAnnotation(Transient.class) == null) {
					target.addAttribute(new ScriptAttribute(field.getType().getName(), field.getName()));
				}
			}
		} catch (Exception ignored) {
		}
	}

	public static void addDefaultImport(String name, Object target) {
		DEFAULT_IMPORTS.put(name, target);
	}

	public static Map<String, Object> getDefaultImports() {
		return DEFAULT_IMPORTS;
	}

	public static Object execute(MagicScript magicScript, MagicScriptContext context) {
		SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
		simpleScriptContext.setAttribute(MagicScript.CONTEXT_ROOT, context, ScriptContext.ENGINE_SCOPE);
		return magicScript.eval(simpleScriptContext);
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return compile(script).eval(context);
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		return compile(reader).eval(context);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return magicScriptEngineFactory;
	}

	@Override
	public CompiledScript compile(String script) {
		return MagicScript.create(script, this);
	}

	@Override
	public CompiledScript compile(Reader script) {
		return compile(readString(script));
	}

	private String readString(Reader reader) {
		StringBuilder builder = new StringBuilder();
		char[] buf = new char[1024];
		int len;
		try {
			while ((len = reader.read(buf, 0, buf.length)) != -1) {
				builder.append(buf, 0, len);
			}
		} catch (IOException ignored) {

		}
		return builder.toString();
	}
}
