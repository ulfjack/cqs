package net.cqs.plugins;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.StartupListener;

@Plugin
public class ServiceTrayPlugin implements StartupListener
{

private static final Logger logger = Logger.getLogger("net.cqs.plugins");

//private static JDialog dialog;
//static
//{
//	dialog = new JDialog((Frame) null);
//	dialog.setUndecorated(true);
//	dialog.setAlwaysOnTop(true);
//}

private final FrontEnd frontEnd;

public ServiceTrayPlugin(PluginConfig config)
{
  this.frontEnd = config.getFrontEnd();
}

private String url() {
  return frontEnd.url();
}

private void startBrowser()
{
	try
	{ Desktop.getDesktop().browse(new URI(url())); }
	catch (IOException e)
	{ e.printStackTrace(); }
	catch (URISyntaxException e)
	{ e.printStackTrace(); }
}

@Override
public void startup()
{
	if (!SystemTray.isSupported())
	{
		logger.info("No Systray found!");
		return;
	}
	try
	{
		SystemTray tray = SystemTray.getSystemTray();
		
		URL imageUrl = ServiceTrayPlugin.class.getClassLoader().getResource("net/cqs/plugins/tray-cqs.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
		final TrayIcon trayIcon = new TrayIcon(image);
		trayIcon.setImageAutoSize(true);
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		final PopupMenu popup = new PopupMenu();
		MenuItem item;
		item = new MenuItem("Open '"+url()+"' in browser...");
		item.addActionListener(new ActionListener()
			{
				@Override
        public void actionPerformed(ActionEvent event)
				{ startBrowser(); }
			});
		popup.add(item);
		
		popup.addSeparator();
		
		item = new MenuItem("Quit");
		item.addActionListener(new ActionListener()
			{
				@Override
        public void actionPerformed(ActionEvent e)
				{ System.exit(0); }
			});
		popup.add(item);
		
		trayIcon.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{ startBrowser(); }
			});
		trayIcon.setPopupMenu(popup);
		
		tray.add(trayIcon);
		logger.info("Tray icon installed.");
		
		// Unfortunately, JPopupMenu doesn't work properly.
		// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6285881
		// the following code is from here:
		// http://weblogs.java.net/blog/alexfromsun/archive/2008/02/jtrayicon_updat.html
		// but it doesn't work properly either, on Xubuntu 8.10.
		// using Java(TM) SE Runtime Environment (build 1.6.0_10-b33)
//		final JPopupMenu jpopup = new JPopupMenu();
//		imageUrl = ServiceTrayPlugin.class.getClassLoader().getResource("net/cqs/plugins/tray-exit.png");
//		JMenuItem item = new JMenuItem("Quit", new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageUrl)));
//		item.addActionListener(new ActionListener()
//			{
//				public void actionPerformed(ActionEvent e)
//				{ System.exit(0); }
//			});
//		jpopup.add(item);
//		trayIcon.addMouseListener(new MouseAdapter()
//			{
//				@Override
//				public void mousePressed(MouseEvent e)
//				{
///*					if (jpopup.isShowing())
//						jpopup.setVisible(false);
//					else*/
//					if (e.isPopupTrigger())
//					{
////						jpopup.setLocation(e.getX(), e.getY());
////						jpopup.setInvoker(dialog);
////						jpopup.setVisible(true);
//						
//						dialog.setLocation(e.getX(), e.getY());
//						dialog.setVisible(true);
//						jpopup.show(dialog.getContentPane(), 0, 0);
//						dialog.toFront();
//					}
//				}
//			});
//		jpopup.addPopupMenuListener(new PopupMenuListener()
//			{
//				@Override
//				public void popupMenuCanceled(PopupMenuEvent e)
//				{ dialog.setVisible(false); }
//				@Override
//				public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
//				{ dialog.setVisible(false); }
//				@Override
//				public void popupMenuWillBecomeVisible(PopupMenuEvent e)
//				{/*Do nothing*/}
//			});
//		
//		tray.add(trayIcon);
	}
	catch (AWTException e)
	{ throw new RuntimeException(e); }
	catch (ClassNotFoundException e)
	{ throw new RuntimeException(e); }
	catch (InstantiationException e)
	{ throw new RuntimeException(e); }
	catch (IllegalAccessException e)
	{ throw new RuntimeException(e); }
	catch (UnsupportedLookAndFeelException e)
	{ throw new RuntimeException(e); }
}

}
