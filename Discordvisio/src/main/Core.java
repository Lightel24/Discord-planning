package main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Core {
	
	private enum State{
		
	}
	
	
	private ArrayList<Visio> visios;
	private HashMap<String,Commande> core = new HashMap<String,Commande>();
	private Visio current;
	
	public Core() {
		initDiscord();
		load();
		show();
		
		new Thread() {				//Thread service, il passe d'une visio à l'autre au cours du temps
			public void run() {
				while (true) {
		        	if(current!=null) {
		        		 if(current.getEndTimeStamp()<System.currentTimeMillis()/1000) {
		 	            	visios.remove(current);
		 	            	current = getNextVisio();
		 	            	show();
		 	            }else if(!current.isActive() && current.getStartTimeStamp()<System.currentTimeMillis()/1000) {
		 	            	show();
		 	            }
		        	}
			       
		            try {
						Thread.sleep(5000);	//On actualise toutes les 5 secondes
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		        }
			}
		}.start();
		
		
		//On initialse les callbacks
		//TEST
		core.put("test", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			System.out.println("Affichage des données test");
                DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Thermo");
                presence.setDetails("Physique");
                presence.setEndTimestamp((System.currentTimeMillis() / 1000)+3600);
                presence.setParty("0000000000000000", 20, 5);
                DiscordRPC.discordUpdatePresence(presence.build());
			}
		}));
		
		//RECHARGER
		core.put("recharger", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			load();
			}
		}));
		
		//Actualiser
		core.put("actualiser", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			show();
			}
		}));
		
		//Aide
		core.put("aide", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			help();
			}
		}));
		
		//Ajouter
		core.put("actualiser", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			add();
			}
		}));
		
		//Ajouter
		core.put("planning", new Commande(new CommandeEvent() {
			@Override
			public void execute() {
    			planning();
			}
		}));
		
	}

	public void execution(String input) {
		
        System.err.println(input);
            if (!input.equalsIgnoreCase("exit")){            	
            	switch(input.toLowerCase()){
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
    	    	System.out.println("\nFormat:	[UNINPLEMENTED]"); 
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

    		private void show() {
    			System.out.println("Actualisation de l'affichage");
    			if(current == null) {
    				 DiscordRPC.discordClearPresence();
    			}else if(current.getStartTimeStamp()>System.currentTimeMillis()/1000) {
    		    		 if(isOtherVisioToday()) {
    		    			 DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(current.getChap());
    			    		 presence.setDetails("Prochaine visio: " + current.getCourse());
    			             presence.setEndTimestamp(current.getStartTimeStamp());
    			             DiscordRPC.discordUpdatePresence(presence.build());
    			             current.setActive(false);
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
    		             current.setActive(true);
    		    	 }
    		}

    		private boolean isOtherVisioToday() {
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
    		
    		
    		private Visio getNextVisio() {
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

    		private void initDiscord() {
    			System.out.println("Initialisation ...");
    			DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
    	        DiscordRPC.discordInitialize("690156162259091459", handlers, false);
    	        DiscordRPC.discordRegister("690156162259091459", "");
    	    }
            
	
}	
