package main;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Loader {
	
	private final static String PATH = "Visiodata/";
	
	public static ArrayList<Visio> loadCsv(){
		ArrayList<Visio> toReturn = new ArrayList<Visio>();
		System.out.println("Chargement des donnees");
		BufferedReader bw = null;
		try {
			bw = new BufferedReader(new FileReader(new File(PATH+"data.csv").getAbsolutePath()));
			String ligne;
			while((ligne = bw.readLine())!=null) {
				System.out.println("Données: " + ligne);
				String[] donnees = ligne.split(";");
				SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			    Date date;
				try {
					date = formatter.parse(donnees[0]);
					System.err.println(new Date(date.getTime()));

				if(date.getTime()/1000 + Integer.parseInt(donnees[1])>System.currentTimeMillis()/1000) {
					Visio visio = new Visio(date.getTime(), Integer.parseInt(donnees[1]), donnees[2], donnees[3], donnees[4]);
					toReturn.add(visio);
					System.out.println("Ajout: "+visio.toString());
				}
				
				} catch (ParseException e) {
					System.err.println(donnees[0] + "	N'est pas un format de date valide. Le format supporte est: 	dd-M-yyyy hh:mm:ss	\nCette entrée a ete omise.");
				}
			 }
		} catch (IOException e) {
			System.out.println("\n\nAucun fichier " + PATH+"data.csv" +" trouve.\n\n\n");
			System.out.println("Format:");
			System.out.println("dd-M-yyyy hh:mm:ss;" + 3600+";Maths;Arithmetic;http://vavoirailleursijysuis.com");
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		return toReturn;
	}
}
