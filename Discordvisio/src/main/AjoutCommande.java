package main;

public class AjoutCommande extends Commande {

	public AjoutCommande() {
		super(new CommandeEvent() {
			@Override
			public void execute() {
				//La logique de la commande
			}
		});
	}
	
	public Visio getVisio() { //Pour renvoyer la visio au core
		return null;
	}
}
