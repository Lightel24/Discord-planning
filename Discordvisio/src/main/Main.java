package main;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Main {

	Console console ;
	Core core;
		public static void main(String[] args) {
			if(args.length>=1) {
				System.err.println("Ouverture par chrome");
				Scanner sc = new Scanner(System.in);
				while(true) {
					System.err.println(sc.nextLine());
				}
			}else {
				System.out.println(ZoneId.systemDefault().toString());
				System.out.println(new Date(System.currentTimeMillis()));
				new Main();
			}
	    }
		
		public Main() {
			console = new Console();
		    console.init();
			core = new Core();
	        System.out.println("Running callbacks...");
		}
		
		
	  

		class Console {
			
			  final JFrame frame = new JFrame("Visio manager");
			   JTextArea textArea = new JTextArea(24, 80);
			   JTextField textfield = new JTextField("Commandes");
			  JScrollPane scroll =  new JScrollPane(textArea);
			  SystemTray tray;
			  TrayIcon trayIcon;
			  
			  public Console() {
			    textArea.setBackground(Color.BLACK);
			    textArea.setForeground(Color.LIGHT_GRAY);
			    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			    textArea.setEditable(false);
			    textArea.setAutoscrolls(true);
			    textfield.setEditable(true);
			    textfield.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent arg0) {
						if(arg0.getKeyCode() == KeyEvent.VK_ENTER && !textfield.getText().isEmpty()) {
							textArea.append("> "+getCommand()+"\n");
							core.execution(textfield.getText());
							textfield.setText("");
						}
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
					}
			    	
			    });
			    System.setOut(new PrintStream(new OutputStream() {
			      @Override
			      public void write(int b) throws IOException {
			        textArea.append(String.valueOf((char) b));
			        textArea.setCaretPosition(textArea.getDocument().getLength());

			      }		
			    }));
			    frame.setLayout(new BorderLayout());
			    frame.add(scroll,BorderLayout.CENTER);
			    frame.add(textfield,BorderLayout.SOUTH);
			    systemTray();
			  }
			  public void init() {
			    frame.pack();
			    frame.setSize(800,400);
			    frame.setVisible(true);
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			  }
			  public JFrame getFrame() {
			    return frame;
			  }
			  public String getCommand() {
				    return textfield.getText();
			}
			  
			  
			  
			  
			  private void systemTray() {

				        if(SystemTray.isSupported()){
				            System.out.println("La fenetre pourra etre cachee dans la barre des taches");
				            tray=SystemTray.getSystemTray();

				            Image image=Toolkit.getDefaultToolkit().getImage("Visiodata/java.png");
				            ActionListener exitListener=new ActionListener() {
				                public void actionPerformed(ActionEvent e) {
				                    System.out.println("Exiting....");
				                    System.exit(0);
				                }
				            };
				            PopupMenu popup=new PopupMenu();
				            MenuItem defaultItem=new MenuItem("Exit");
				            defaultItem.addActionListener(exitListener);
				            popup.add(defaultItem);
				            defaultItem=new MenuItem("Open");
				            defaultItem.addActionListener(new ActionListener() {
				                public void actionPerformed(ActionEvent e) {
				                    frame.setVisible(true);
				                    frame.setExtendedState(JFrame.NORMAL);
				                }
				            });
				            popup.add(defaultItem);
				            trayIcon=new TrayIcon(image, "Visio manager", popup);
				            trayIcon.setImageAutoSize(true);
				        }else{
				            System.out.println("system tray not supported");
				        }
				        frame.addWindowStateListener(new WindowStateListener() {
				            public void windowStateChanged(WindowEvent e) {
				                if(e.getNewState()==JFrame.ICONIFIED){
				                    try {
				                        tray.add(trayIcon);
				                        frame.setVisible(false);
				                    } catch (AWTException ex) {
				                        System.out.println("unable to add to tray");
				                    }
				                }
				        if(e.getNewState()==7){
				                    try{
				            tray.add(trayIcon);
				            frame.setVisible(false);
				            }catch(AWTException ex){
				            System.out.println("unable to add to system tray");
				        }
				            }
				        if(e.getNewState()==JFrame.MAXIMIZED_BOTH){
				                    tray.remove(trayIcon);
				                    frame.setVisible(true);
				                }
				                if(e.getNewState()==JFrame.NORMAL){
				                    tray.remove(trayIcon);
				                    frame.setVisible(true);
				                }
				            }
				        });
			  }
		}
}
