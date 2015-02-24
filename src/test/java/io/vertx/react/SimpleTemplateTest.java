package io.vertx.react;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.vertx.core.json.JsonObject;
import io.vertx.react.filesystem.ClasspathFileResolver;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTemplateTest {

    private static ScriptEngine nashorn;

    @BeforeClass
    public static void setUp() throws ScriptException {
        ClasspathFileResolver.init();
        ScriptEngineManager mgr = new ScriptEngineManager();
        nashorn = mgr.getEngineByName("nashorn");
        nashorn.eval(getScript("jvm-npm.js")); // tweaked version of vertx's
                                               // jvm-npm
        nashorn.eval(getScript("vertx-js/util/console.js"));
        nashorn.eval(getScript("vertx-js/util/utils.js"));
        nashorn.eval("var process = {env:{}}");
        nashorn.eval("var global = this;");
        nashorn.eval(getBundledReact());
    }

    @Test
    public void testRenderReactComponent() throws ScriptException {
        nashorn.eval("var React = require('react')");
        String name = "Foo";
        String componentName = "MyComponent";
        createPureReactComponent(componentName);
        String snippet = renderComponentWithProps(componentName, new JsonObject().put("name", name));
        assertTrue(snippet.startsWith("<div data-reactid="));
        assertTrue(snippet.endsWith(">" + name + "</div>"));
    }

    @Test
    public void testRenderReactComponentToStaticMarkup() throws ScriptException {
        nashorn.eval("var React = require('react')");
        String name = "Foo";
        String componentName = "MyComponent";
        createPureReactComponent(componentName);
        String snippet = renderToStaticMarkupWithProps(componentName, new JsonObject().put("name", name));
        assertEquals("<div>Foo</div>", snippet);
    }

    // private static String requireInJs(String classPathFileName) {
    // return classPathFileName.replaceAll("\\\\", "/");
    // }
    //
    // private static String getReactJs() {
    // String classPathFileName =
    // ClasspathFileResolver.resolveFilename("node_modules/react/react.js");
    // return requireInJs(classPathFileName);
    // }
    //
    private static Reader getBundledReact() {
        return getScript("node_modules/react/dist/react-with-addons.js");
    }

    private static Reader getScript(String path) {
        return new InputStreamReader(SimpleTemplateTest.class.getClassLoader().getResourceAsStream(path));
    }

    private void createPureReactComponent(String componentName) throws ScriptException {
        StringBuilder sb = new StringBuilder();
        sb.append("var " + componentName + " = React.createClass({\n");
        sb.append("   render: function(){\n");
        sb.append("       return React.DOM.div(null, this.props.name)\n");
        sb.append("   }\n");
        sb.append("});\n");
        nashorn.eval(sb.toString());
    }

    private String renderComponentWithProps(String componentName, JsonObject props) throws ScriptException {
        String renderToString = "React.renderToString(React.createFactory(" + componentName + ")(" + props.toString() + "))";
        return (String) nashorn.eval(renderToString);
    }

    private String renderToStaticMarkupWithProps(String componentName, JsonObject props) throws ScriptException {
        String renderToMarkup = "React.renderToStaticMarkup(React.createFactory(" + componentName + ")(" + props.toString() + "))";
        return (String) nashorn.eval(renderToMarkup);
    }
}
