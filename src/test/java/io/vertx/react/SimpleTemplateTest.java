package io.vertx.react;

import io.vertx.react.filesystem.ClasspathFileResolver;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

public class SimpleTemplateTest {

	private ScriptEngine nashorn;

	@Before
	public void setUp() throws ScriptException {
		ClasspathFileResolver.init();
		ScriptEngineManager mgr = new ScriptEngineManager();
		nashorn = mgr.getEngineByName("nashorn");
		nashorn.eval(getScript("jvm-npm.js"));
		nashorn.eval(getScript("vertx-js/util/console.js"));
		nashorn.eval(getScript("vertx-js/util/utils.js"));
		nashorn.eval("var global = this;");
	}

	@Test
	public void testSimpleTemplate() throws ScriptException {
		nashorn.eval("require('"
				+ ClasspathFileResolver
						.resolveFilename("node_modules/react/react.js") + "');");
		// nashorn.eval(getScript("node_modules/react/react.js"));
	}

	private Reader getScript(String path) {
		return new InputStreamReader(this.getClass().getClassLoader()
				.getResourceAsStream(path));
	}
}
