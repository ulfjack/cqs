package net.cqs.plugins.rhino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;

import net.cqs.auth.Identity;
import net.cqs.services.AuthService;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.tools.ToolErrorReporter;

public final class RhinoThread implements Runnable
{

private Context cx;
private Scriptable scope;
private Socket client;
private PrintStream log;
private RhinoServer rp;
private int num;
private boolean shutdown;
private RhinoInterface ri;
private boolean synchronize = true;

public class RhinoInterface
{
	PrintStream out;
	
	RhinoInterface(PrintStream out)
	{
		this.out = out;
		synchronize = false;
	}
	
	public void output(boolean b)
	{ out.println(b); }
	
	public void output(Object o)
	{ out.println(o.toString()); }
	
	public void output(int i)
	{ out.println(i); }
}

public RhinoThread(Socket myClient, PrintStream myLog, int myNum, RhinoServer myRp)
{
	client = myClient;
	log = myLog;
	num = myNum;
	rp = myRp;
	shutdown = false;
	Thread t = new Thread(this, "RhinoThread: "+num+" ("+client.getInetAddress()+")");
	t.start();
}

public void loadConfiguration(PrintStream out)
{
	try
	{
		String filename = "net/cqs/plugins/rhino/defaults.js";
		InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
		Reader r = new InputStreamReader(in);
		evaluateReader(r, out, filename, 0);
	}
	catch (Exception e)
	{ e.printStackTrace(out); }
}

@Override
public void run()
{
	log.println("[RHINO] Connection opened to "+num+","+client.getInetAddress());
	
	try
	{
		try
		{
			cx = Context.enter();
			BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
			PrintStream out = new PrintStream(client.getOutputStream());
			if (auth(in, out))
			{
				ri = new RhinoInterface(out);
				loadPredefines();
				loadConfiguration(out);
				out.println(rp.frontEnd.version());
				out.println("Welcome to the CQS Rhino Plugin. Your ID is "+num+". \n"
				    +"Your security clearance conforms to 'Jaeger inner Schaumstoffpackung'.\n"
				    +"Use NOSYNC to unsync from database.\n");
				exec(in, out);
			}
		}
		catch (Exception e)
		{ if (!shutdown) e.printStackTrace(log); }
		
		if (!shutdown) try
		{
			client.close();
		}
		catch (Exception e)
		{ e.printStackTrace(log); }
		
		log.println("[RHINO] Connection closed ("+num+","+client.getInetAddress()+")!");
		Context.exit();
	}
	catch (Exception e)
	{
		e.printStackTrace(log);
	}
	rp.removeThread(this);
}

public void loadPredefines()
{
	scope = cx.initStandardObjects(null);
	
	Scriptable sim = Context.toObject(rp.frontEnd.getGalaxy(), scope);
	scope.put("sim", scope, sim);
	
	Scriptable rhino = Context.toObject(ri, scope);
	scope.put("rhino", scope, rhino);
}

public boolean auth(BufferedReader in, PrintStream out) throws IOException
{
	out.println("Welcome. Please authentify yourself.");
	out.print("login: ");
	String user = in.readLine();
	out.print("pass: ");
	String pass = in.readLine();
	
	AuthService authService = rp.frontEnd.findService(AuthService.class);
	Identity id = authService.authenticate(user, pass);
	return authService.isInGroup(id, "staff");
}

public void shutdown()
{
	this.shutdown = true;
	try
	{
		client.close();
	}
	catch (Exception ignored)
	{/*OK*/}
}

void exec(BufferedReader in, PrintStream out) throws IOException
{
	cx.setErrorReporter(new ToolErrorReporter(false, out));
	cx.setOptimizationLevel(-1);
	
	int lineno = 1;
	while (true)
	{
		int startline = lineno;
		out.print("rhino> ");
		log.print(num+"@rhino> ");
		out.flush();
		
		String source = in.readLine();
		lineno++;
		
		log.println(source);
		
		if ("NOSYNC".equals(source)) 
		{
			synchronize = false;
			out.println("Forcing NOSYNC: Better be careful. Schaumstoffpackung deaktivated.");
			continue;
		}
		
		if (source == null) return;
		Reader reader = new StringReader(source);
		Object result = evaluateReader(reader, out, "<stdin>", startline);
		
		if (result != Context.getUndefinedValue())
		{
			try
			{
				String s = Context.toString(result);
				out.println(s);
				log.println(s);
			}
			catch (EcmaError ee)
			{
				String msg = ToolErrorReporter.getMessage(
				    "msg.uncaughtJSException", ee.toString());
				if (ee.sourceName() != null)
				{
					Context.reportError(msg, ee.sourceName(), ee.lineNumber(),
						ee.lineSource(), ee.columnNumber());
				}
				else
					Context.reportError(msg);
			}
		}
	}
}

Object evaluateReader(Reader in, PrintStream out, String sourceName, int lineno)
{
	Object result = Context.getUndefinedValue();
	try
	{
		if (synchronize)
		{
			synchronized (rp.frontEnd.getGalaxy())
			{
				result = cx.evaluateReader(scope, in, sourceName, lineno, null);
			}
		}
		else
		{
			result = cx.evaluateReader(scope, in, sourceName, lineno, null); 
		}
	}
	catch (WrappedException we)
	{
		out.println(we.getWrappedException().toString());
		we.printStackTrace(log);
	}
	catch (EcmaError ee)
	{
		String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ee.toString());
		if (ee.sourceName() != null)
		{
			Context.reportError(msg, ee.sourceName(), ee.lineNumber(),
				ee.lineSource(), ee.columnNumber());
		}
		else
			Context.reportError(msg);
	}
	catch (EvaluatorException ee)
	{ ee.printStackTrace(); }
	catch (JavaScriptException jse)
	{
		Object value = jse.getValue();
		if (value instanceof ThreadDeath)
			throw (ThreadDeath) value;
		Context.reportError(ToolErrorReporter.getMessage(
		"msg.uncaughtJSException", jse.getMessage()));
	}
	catch (IOException ioe)
	{ out.println(ioe.toString()); }
	return result;
}

}

