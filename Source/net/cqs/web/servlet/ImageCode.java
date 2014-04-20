package net.cqs.web.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.ofahrt.catfish.utils.MimeType;

public final class ImageCode extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final int WIDTH = 100;
private static final int HEIGHT = 40;

private static final BufferedImage IMG =
        new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
private static final Graphics2D GRAPHICS = IMG.createGraphics();

private static final ImageWriter IMAGE_WRITER =
        ImageIO.getImageWritersByFormatName("jpg").next();

private static final Color BACKGROUND_COLOR = new Color(25, 10, 70);
private static final Color FOREGROUND_COLOR = new Color(192, 192, 192);

private static final Font FONT = new Font("Lucida Sans", Font.ITALIC, 38);
private static final FontMetrics FONT_METRICS;

private static final Random RAND = new Random();

static
{
	FONT_METRICS = GRAPHICS.getFontMetrics(FONT);
	
	GRAPHICS.setBackground(BACKGROUND_COLOR);
	GRAPHICS.setColor(FOREGROUND_COLOR);
	GRAPHICS.clearRect(0, 0, WIDTH, HEIGHT);
	GRAPHICS.setFont(FONT);
}

private static Color[] colors = new Color[]
	{ Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE };

private static void write(String code, OutputStream os)
	throws IOException
{
	int ascent = FONT_METRICS.getAscent();
	int descent = FONT_METRICS.getDescent();
	
	int dx = FONT_METRICS.stringWidth(code);
	int dy = ascent+descent;
	
	int xpos = (WIDTH-dx)/2;
	int ypos = (HEIGHT-dy)/2+ascent;
	
	GRAPHICS.setColor(Color.BLACK);
	GRAPHICS.clearRect(0, 0, WIDTH, HEIGHT);
	
	for (int i = 0; i < 5; i++)
	{
		{
			int x0 = RAND.nextInt((WIDTH*3)/2)-WIDTH/2;
			int y0 = RAND.nextInt((HEIGHT*3)/2)-HEIGHT/2;
			int x1 = RAND.nextInt(WIDTH/2)+WIDTH/2;
			int y1 = RAND.nextInt(HEIGHT/2)+HEIGHT/2;
			
			GRAPHICS.setXORMode(colors[RAND.nextInt(colors.length)]);
			GRAPHICS.fillRect(x0, y0, x1-x0, y1-y0);
		}
		
/*		{
			int x0 = rand.nextInt(width);
			int y0 = rand.nextInt(height);
			int x1 = rand.nextInt(width);
			int y1 = rand.nextInt(height);
			
			g.setXORMode(colors[rand.nextInt(colors.length)]);
			g.drawLine(x0, y0, x1, y1);
		}*/
	}
	
	GRAPHICS.setXORMode(Color.WHITE);
//	GRAPHICS.drawString(code, xpos-1, ypos-1);
	GRAPHICS.drawString(code, xpos, ypos);

	IMAGE_WRITER.setOutput(ImageIO.createImageOutputStream(os));
	IMAGE_WRITER.write(IMG);
}

private static String data = "123456789abcdefghijklmnopqrstuvxyz";

private static String generateCode()
{
	StringBuffer result = new StringBuffer();
	for (int i = 0; i < 4; i++)
		result.append(data.charAt(RAND.nextInt(data.length())));
	return result.toString();
}

private static final String KEY = "net.cqs.web.catfish.ImageCode";
private static final long TIMEOUT = 5*60*1000L;

public ImageCode()
{/*OK*/}

@Override
public synchronized void doGet(HttpServletRequest req, HttpServletResponse res)
{
	try
	{
		String code = generateCode();
		
		req.getSession().setAttribute(KEY, code);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType(MimeType.IMAGE_JPEG.toString());
		OutputStream out = res.getOutputStream();
		write(code, out);
		out.close();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
}

public static boolean isValid(HttpServletRequest req, String key)
{
	if (key == null) return false;
	HttpSession session = req.getSession();
	long time = System.currentTimeMillis();
	if (time-session.getLastAccessedTime() > TIMEOUT)
		session.removeAttribute(KEY);
	String code = (String) session.getAttribute(KEY);
	if (key.equals(code)) return true;
	return false;
}

}
