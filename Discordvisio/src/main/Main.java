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
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Main {
	
    private static boolean ready = false;
	private static ArrayList<Visio> visios;
	private static Visio current;
	Console console ;
		public static void main(String[] args) {
			System.out.println(ZoneId.systemDefault().toString());
			System.out.println(new Date(System.currentTimeMillis()));
			
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

		        if(current.getEndTimeStamp()<System.currentTimeMillis()/1000) {
	            	visios.remove(current);
	            	current = getNextVisio();
	            	show();
	            }else if(current.getStartTimeStamp()<System.currentTimeMillis()/1000) {
	            	show();
	            }
	            try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
		
		private void execution() {
			
            String input = console.getCommand();
            System.err.println(input);
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
	            		case"ajouter":
	            			add();
	            			break;
	            		default:
	            			System.out.println("\nCommande inconnue.\"Aide\" pour obtenir une liste des commandes.\n");
	            	}
	            } else {
	                System.exit(0);
	            }
		}
		
	    private void add() {//TODO
	    	System.out.println("\nFormat:	(retirer les espaces)	[UNINPLEMENTED]"); 
	    	System.out.println("\ndd-M-yyyy hh:mm:ss; Duree(s) ; Matiere ; Chapitre ; Site(vous pouvez mettre n'imp)	\nExemple: ");
	    	System.out.println("23-03-2020 08:00:00;7200;Maths;Espaces vectoriels;https://eu.bbcollab.com/collab/seesion/seesionid");
			
		}

		private void planning() {
			System.out.println("\nListe des visios chargées:\n");
	    	for(Visio visio: visios) {
	    		System.out.println(visio.toString()+"\n");
			}
			
		}

		private void help() {
			System.out.println("\nAide! Voici la liste des commandes:\n-aide\n-test\n-recharger\n-actualiser\n-planning\n-ajouter");

		}

		private static void show() {
			System.out.println("Actualisation de l'affichage");
			if(current == null) {
				 DiscordRPC.discordClearPresence();
    			 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Pas de visio aujourd'hui");
	             DiscordRPC.discordUpdatePresence(presence.build());
			}else if(current.getStartTimeStamp()>System.currentTimeMillis()/1000) {
	    		 if(isOtherVisioToday()) {
	    			 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(current.getChap());
		    		 presence.setDetails("Visio suivante " + current.getCourse());
		             presence.setEndTimestamp(current.getStartTimeStamp());
		             DiscordRPC.discordUpdatePresence(presence.build());
	    		 }else {
	    			 DiscordRPC.discordClearPresence();
	    			 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Pas de visio aujourd'hui");
		             DiscordRPC.discordUpdatePresence(presence.build());
	    		 }
	    	 }else {
		    	 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(current.getChap());
	    		 presence.setDetails(current.getCourse());
	             presence.setEndTimestamp(current.getEndTimeStamp());
	             presence.setSecrets("", current.getLink());
	             DiscordRPC.discordUpdatePresence(presence.build());
	    	 }
		}

		private static boolean isOtherVisioToday() {
			Calendar date = new GregorianCalendar();
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
			//Demain
			date.add(Calendar.DAY_OF_MONTH, 1);
			Date demain = date.getTime();
			
			return (demain.compareTo(new Date(getNextVisio().getStartTimeStamp()*1000))>0);  //Si minuit suivant est après la prochaine visio, donc y en a  ajd	True			
		}

		private void load() {
			System.out.println("Chargement");
	    	visios = Loader.loadCsv();
	    	current = getNextVisio();
		}
		
		private static Visio getNextVisio() {
			if(visios.size()!=0) {
				Visio next =visios.get(0);
				for (int i =1; i< visios.size();i++){
					if(visios.get(i).getStartTimeStamp()<next.getStartTimeStamp()) {
						next = visios.get(i);
					}
				}
				return next;
			}else {
				System.out.println("\n\nIl n'y a plus de visios programmée.");
				return null;
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
	        ready = true;
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
