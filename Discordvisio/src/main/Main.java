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
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Main {
	
    private static boolean ready = false;
	private ArrayList<Visio> visios;
	private static Visio current;
	Console console ;
		public static void main(String[] args) {
			new Main();
	    }
		
		public Main() {
			console = new Console();
		    console.init();
	        initDiscord();
            load();
            show();
	        System.out.println("Running callbacks...");
	       while (true) {
	            if(!ready)
	                continue;

	            DiscordRPC.discordRunCallbacks();
	            try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
		
		private void execution() {
			
            String input = console.getCommand();
            System.err.print(input);
	            if (!input.equalsIgnoreCase("exit")){
	            	switch(input.toLowerCase()){
	            		case"test":
	            			test();
	            			break;
	            		case"recharger":
	            			load();
	            			break;
	            		case"actualiser":
	            			show();
	            			break;
	            		case"aide":
	            			help();
	            			break;
	            		case"planning":
	            			planning();
	            			break;
	            		default:
	            			System.out.println("\nCommande inconnue.\"Aide\" pour obtenir une liste des commandes.\n");
	            	}
	            } else {
	                System.exit(0);
	            }
		}
		
	    private void planning() {
			System.out.println("\nListe des visios chargées:\n");
	    	for(Visio visio: visios) {
	    		System.out.println(visio.toString()+"\n");
			}
			
		}

		private void help() {
			System.out.println("\nAide! Voici la liste des commandes:\n-aide\n-test\n-recharger\n-actualiser");

		}

		private static void show() {
			System.out.println("Actualisation de l'affichage");
	    	 if(current.getStartTimeStamp()>System.currentTimeMillis()/1000) {
	    		 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(current.getChap());
	    		 presence.setDetails("Visio suivante " + current.getCourse());
	             presence.setEndTimestamp(current.getEndTimeStamp());
	             DiscordRPC.discordUpdatePresence(presence.build());
	    	 }else {
		    	 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(current.getChap());
	    		 presence.setDetails(current.getCourse());
	             presence.setEndTimestamp(current.getEndTimeStamp());
	             presence.setSecrets("", current.getLink());
	             DiscordRPC.discordUpdatePresence(presence.build());
	    	 }
		}

		private void load() {
			System.out.println("Chargement");
	    	visios = Loader.loadCsv();
	    	current =visios.get(0);
			for (int i =1; i< visios.size();i++){
				if(visios.get(i).getStartTimeStamp()<current.getStartTimeStamp()) {
					current = visios.get(i);
				}
			}
		}

		private void test() {
			System.out.println("Affichage des données test");
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Thermo");
             presence.setDetails("Physique");
             presence.setEndTimestamp((System.currentTimeMillis() / 1000)+3600);
             presence.setParty("0000000000000000", 20, 5);
             DiscordRPC.discordUpdatePresence(presence.build());
		}

		private static void initDiscord() {
			System.out.println("Initialisation ...");
	        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
	        DiscordRPC.discordInitialize("690156162259091459", handlers, false);
	        DiscordRPC.discordRegister("690156162259091459", "");
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
							execution();
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
