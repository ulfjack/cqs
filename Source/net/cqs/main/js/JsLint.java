package net.cqs.main.js;

import java.io.IOException;

import net.cqs.main.Application;

import org.mozilla.javascript.tools.shell.Main;

public class JsLint implements Application
{

public JsLint()
{
	// Ok for now.
}

@Override
public void run(String[] args) throws IOException
{
	Main.main(new String[] {"Libraries/jslint.js", args[1]});
}

public static void main(String[] args) throws IOException
{
	new JsLint().run(new String[] {"", "Html/Design/js/common.js"});
}

}
