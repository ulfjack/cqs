package net.cqs.plugins.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.services.email.Email;

public final class SmtpClient implements Runnable
{

private static final Logger logger = Logger.getLogger("net.cqs.plugins");

public static final int MAX_TRY = 5;

private String server = "";
private int port = 25;

private final String senderAddress;

private Socket client;
private SmtpWriter out;
private BufferedReader in;

private List<EmailInfo> data = new LinkedList<EmailInfo>();

public SmtpClient(String server, String senderAddress)
{
	this.server = server;
	this.senderAddress = senderAddress;
	new Thread(this).start();
}

private void getPositiveResponse(BufferedReader inReader) throws IOException
{
	out.flush();
	String s = inReader.readLine();
	if (!s.startsWith("2")) throw new IOException("No Positive Response!");
}

private void getGoAheadResponse(BufferedReader inReader) throws IOException
{
	out.flush();
	String s = inReader.readLine();
	if (!s.startsWith("354")) throw new IOException("No Positive Response!");
}

private String translate(String s)
{
	StringBuffer buf = new StringBuffer(s);
	if (buf.charAt(0) == '.')
		buf.insert(0, '.');
	int i = 0;
	try
	{
		while (i < buf.length())
		{
			if (buf.charAt(i) == 13)
			{
				if (buf.charAt(i+1) == 10)
				{
					if (buf.charAt(i+2) == '.')
					{
						buf.insert(i+2, '.');
					}
				}
			}
			i++;
		}
	}
	catch (IndexOutOfBoundsException e)
	{ e.printStackTrace(); }
	return buf.toString();
}

private void connect() throws IOException
{
	client = new Socket(server, port);
	OutputStreamWriter temp = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
	out = new SmtpWriter(temp);
	in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
	
	getPositiveResponse(in);
	out.println("HELO localhost");
	getPositiveResponse(in);
}

private void sendSingle(Email em) throws IOException
{
	out.println("MAIL FROM: "+senderAddress);
	getPositiveResponse(in);
	out.println("RCPT TO: "+em.getRecipient());
	getPositiveResponse(in);
	out.println("DATA");
	getGoAheadResponse(in);
	out.println("From: "+senderAddress);
	out.println("To: "+em.getRecipient());
	out.println("Subject: "+em.getSubject());
	out.println("Content-Type: text/plain; charset=utf-8");
	out.println();
	out.println(translate(em.getBody()));
	out.println(".");
	getPositiveResponse(in);
	
	logger.info("Message delivered for "+em.getRecipient());
}

private void quit() throws IOException
{
	out.println("QUIT");
	getPositiveResponse(in);
}

private synchronized EmailInfo getWait()
{
	while (data.size() == 0)
		try
		{ wait(1000); }
		catch (InterruptedException e)
		{ e.printStackTrace(); }
	return data.get(0);
}

private synchronized EmailInfo get()
{
	if (data.size() == 0)
		return null;
	else
		return data.get(0);
}

private synchronized void remove()
{ data.remove(0); }

private synchronized void send(EmailInfo email)
{ data.add(email); }

public synchronized void send(Email em)
{ send(new EmailInfo(em)); }

@Override
public void run()
{
	while (true)
	{
		EmailInfo em = getWait();
		try
		{
			connect();
			while ((em = get()) != null)
			{
				try
				{
					sendSingle(em.email);
				}
				catch (Exception e)
				{
					logger.log(Level.WARNING,
							"Sending email to \""+em.email.getRecipient()+"\" failed "+em.tries+". attempt.",
							e);
					em.tries++;
					if (em.tries < MAX_TRY) send(em);
				}
				remove();
			}
			quit();
		}
		catch (Exception e)
		{ logger.log(Level.SEVERE, "Exception caught", e); }
		finally
		{
			try
			{
				in.close();
				out.close();
			}
			catch (Exception e)
			{/*OK*/}
			
			try
			{ client.close(); }
			catch (Exception e)
			{/*OK*/}
		}
		
		try
		{
			synchronized (this)
			{ wait(5000); }
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}
}

}
