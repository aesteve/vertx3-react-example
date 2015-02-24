package io.vertx.react;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
        nashorn.eval(getScript("jvm-npm.js")); // tweaked version of vertx's
                                               // jvm-npm
        nashorn.eval(getScript("vertx-js/util/console.js"));
        nashorn.eval(getScript("vertx-js/util/utils.js"));
        nashorn.eval("var process = {env:{}}");
        nashorn.eval("var global = this;");
    }

    @Test
    public void testRequireReact() throws ScriptException {

        nashorn.eval("require('" + getReactJs() + "');"); // load react, should
                                                          // export 'react'
                                                          // module
                                                          // (and not throw an
                                                          // exception)
        Object react = nashorn.eval("require('react');");
        assertNotNull(react);
    }

    @Test
    public void testRenderReactComponent() throws ScriptException {
        nashorn.eval("var React = require('" + getReactJs() + "');");
        String reactComponent = "var HelloWorldComponent = React.createClass({" + "   render: function(){" + "       return React.DOM.div(null, this.props.name)" + "   }" + "});";
        nashorn.eval(reactComponent);
        String snippet = (String) nashorn.eval("React.renderToString(React.createFactory(HelloWorldComponent)({name:'Visitor'}))");
        assertTrue(snippet.startsWith("<div data-reactid="));
        assertTrue(snippet.endsWith(">Visitor</div>"));
    }

    private Reader getScript(String path) {
        return new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path));
    }

    private String requireInJs(String classPathFileName) {
        return classPathFileName.replaceAll("\\\\", "/");
    }

    private String getReactJs() {
        String classPathFileName = ClasspathFileResolver.resolveFilename("node_modules/react/react.js");
        return requireInJs(classPathFileName);
    }
}
