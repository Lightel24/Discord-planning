package main;

public class Commande{

	private CommandeEvent code;
	
	public Commande(CommandeEvent code) {
		this.code = code;
	}
	
	public void execute() {
		code.execute();
	}

}
