package fr.isika.cda15.projet1.annuaire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ArbreStagiaire {
	
	/**
	 * Définition des attributs
	 * A voir l'intérêt
	 */
	static final int NOM = 25;
	static final int PRENOM = 20;
	static final int DEPARTEMENT = 3;
	static final int PROMO = 15;
	static final int ANNEE_ENTREE = 4;
	static final String PATH_FILE_BIN = "src/main/resources/stagiaires.bin";
	static final String PATH_FILE_DON = "src/main/resources/STAGIAIRES.DON";
	private static int racine = -1;
	private static RandomAccessFile raf;

//************************* Manipulation fichiers binaires ************************** //
	
	public static void initArbre() {
		File monFichierBin = new File(PATH_FILE_BIN);
		File monFichierDon = new File(PATH_FILE_DON);
		if(!monFichierBin.exists()) {
			try {
				raf = new RandomAccessFile(PATH_FILE_BIN, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			initLectureDon(monFichierDon);
		}else {
			try {
				raf = new RandomAccessFile(PATH_FILE_BIN, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			racine = 0;
		}
	}

//	************************** Initialisation de l'arbre à partir d'un fichier bin ************************** //
	
	private static void initLectureDon(File monFichier) {
		String nom = "", prenom = "", departement = "", nomPromo = "", anneeEntree = "";
		try {
			FileReader fr = new FileReader(monFichier);
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				nom = br.readLine();
				prenom = br.readLine();
				departement = br.readLine();
				nomPromo = br.readLine();
				anneeEntree = br.readLine();
				br.readLine();
				ajouter(new Stagiaire(nom, prenom, departement, nomPromo, anneeEntree));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	************************** Méthodes pour la lecture et l'écriture dans le fichier ************************** //
	
	private static void ecritureNoeudFichier(Stagiaire x, int indexPere) {
		int index = 0;
		try {
			if(indexPere!=-1) {
				if (x.compareTo(lectureStagiaire(indexPere)) < 0) {
					raf.seek((indexPere * 142) + 134);
				} else {
					raf.seek((indexPere * 142) + 138);
				}
				index = (int)raf.length() / 142;
				raf.writeInt(index);
				raf.seek(raf.length());
			}else raf.seek(0); // si indexPere = -1, la racine change
			raf.writeChars(x.getNom());
			for (int i = x.getNom().length(); i < 25; i++) {
				raf.writeChars("*");
			}
			raf.writeChars(x.getPrenom());
			for (int i = x.getPrenom().length(); i < 20; i++) {
				raf.writeChars("*");
			}
			raf.writeChars(x.getDepartement());
			for (int i = x.getDepartement().length(); i < 3; i++) {
				raf.writeChars("*");
			}
			raf.writeChars(x.getPromo());
			for (int i = x.getPromo().length(); i < 15; i++) {
				raf.writeChars("*");
			}
			raf.writeChars(x.getAnneeEntree());
			for (int i = x.getAnneeEntree().length(); i < 4; i++) {
				raf.writeChars("*");
			}
			raf.writeInt(-1);
			raf.writeInt(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Stagiaire lectureStagiaire(int index) {
		Stagiaire stagiaireTemporaire = new Stagiaire("", "", "", "", "");
		String motTemporaire = "";
		try {
			raf.seek(index*142);
			for (int i = 0; i < 25; i++) {
				motTemporaire += raf.readChar();
			}
			stagiaireTemporaire.setNom(motTemporaire.replace("*", ""));
			motTemporaire = "";
			for (int i = 0; i < 20; i++) {
				motTemporaire += raf.readChar();
			}
			stagiaireTemporaire.setPrenom(motTemporaire.replace("*", ""));
			motTemporaire = "";
			for (int i = 0; i < 3; i++) {
				motTemporaire += raf.readChar();
			}
			stagiaireTemporaire.setDepartement(motTemporaire.replace("*", ""));
			motTemporaire = "";
			for (int i = 0; i < 15; i++) {
				motTemporaire += raf.readChar();
			}
			stagiaireTemporaire.setPromo(motTemporaire.replace("*", ""));
			motTemporaire = "";
			for (int i = 0; i < 4; i++) {
				motTemporaire += raf.readChar();
			}
			stagiaireTemporaire.setAnneeEntree(motTemporaire.replace("*", ""));
			motTemporaire = "";
			raf.readInt();
			raf.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stagiaireTemporaire;
	}
	
	private static int lectureIndexFilsG(int index) {
		int retour = 0;
		try {
			raf.seek((index*142)+134);
			retour = raf.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retour;
	}
	
	private static int lectureIndexFilsD(int index) {
		int retour = 0;
		try {
			raf.seek((index*142)+138);
			retour = raf.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retour;
	}
	
	private static void ecrireIndexFilsG(int index, int nouveauIndex) {
		try {
			raf.seek((index*142)+134);
			raf.writeInt(nouveauIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void ecrireIndexFilsD(int index, int nouveauIndex) {
		try {
			raf.seek((index*142)+138);
			raf.writeInt(nouveauIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void nouvelleRacine(Stagiaire x, int filsG, int filsD) {
		ecritureNoeudFichier(x, -1);
		try {
			raf.seek(134);
			raf.writeInt(filsG);
			raf.seek(138);
			raf.writeInt(filsD);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	************************** Méthodes pour ajout de Noeud ************************** //	
	
	public static void ajouter(Stagiaire x) {
		ajouterNoeud(x, racine, -1);
	}
	
	private static void ajouterNoeud(Stagiaire x, int courant, int indexPere) {
		if(courant == -1) {
			ecritureNoeudFichier(x, indexPere);
			racine = 0;
		}else if (x.compareTo(lectureStagiaire(courant)) < 0) {
			ajouterNoeud(x, lectureIndexFilsG(courant), courant);
		}else if (x.compareTo(lectureStagiaire(courant)) > 0) {
			ajouterNoeud(x, lectureIndexFilsD(courant), courant);
		}
	}
	
//	************************** Méthodes pour suppression de Noeud ************************** //	
	
	public static void supprimer(Stagiaire x) {
		int racineAvant = racine;
		racine = supprimerNoeud(x, racine);
		if(racineAvant != racine && racine != -1) {
			nouvelleRacine(lectureStagiaire(racine), lectureIndexFilsG(racine), lectureIndexFilsD(racine));
			racine = 0;
		}
	}
	
	private static int supprimerNoeud(Stagiaire x, int courant) {
        if (courant == -1)
            return 0;
        if (lectureStagiaire(courant).compareTo(x) == 0) {
            return supprimerRacine(x, courant);
        }else if (lectureStagiaire(courant).compareTo(x) > 0) {
            ecrireIndexFilsG(supprimerNoeud(x, lectureIndexFilsG(courant)), courant);
            if(lectureIndexFilsG(courant) == -1) ecrireIndexFilsG(courant, -1);
            else ecrireIndexFilsG(courant, lectureIndexFilsG(courant));
        }else {
        	ecrireIndexFilsG(supprimerNoeud(x, lectureIndexFilsD(courant)), courant);
        	if(lectureIndexFilsG(courant) == -1) ecrireIndexFilsD(courant, -1);
        	else ecrireIndexFilsD(courant, lectureIndexFilsG(courant));
        }
        return courant;
    }
	
	private static int supprimerRacine(Stagiaire x, int courant) {
		if (lectureIndexFilsG(courant) == -1 && lectureIndexFilsG(courant) == -1) {
			return -1;
		}else if (lectureIndexFilsG(courant) == -1) {
			ecrireIndexFilsD(courant, lectureIndexFilsD(courant));
            return lectureIndexFilsD(courant);
        }else if (lectureIndexFilsD(courant) == -1) {
        	ecrireIndexFilsG(courant, lectureIndexFilsG(courant));
            return lectureIndexFilsG(courant);
        }else {
        	int dernierDescendant = dernierDescendant(lectureIndexFilsG(courant));
        	
        	ecritureNoeudFichier(lectureStagiaire(dernierDescendant), courant);
        	courant = dernierDescendant;
        	
        	if(courant != lectureIndexFilsD(courant)) ecrireIndexFilsD(courant, lectureIndexFilsD(courant));
        	else ecrireIndexFilsD(courant, -1);
        	if(courant != lectureIndexFilsG(courant)) ecrireIndexFilsG(courant, lectureIndexFilsG(courant));
        	else ecrireIndexFilsG(courant, -1);
        	
        	ecrireIndexFilsG(supprimerNoeud(lectureStagiaire(dernierDescendant), lectureIndexFilsG(courant)), courant);
        }
        return courant;
    }

    private static int dernierDescendant(int courant) {
        if (lectureIndexFilsD(courant) == -1)
            return courant;
        return dernierDescendant(lectureIndexFilsD(courant));
    }

//	************************** Méthodes pour modification de Noeud ************************** //
    
    public static void modifierNom(Stagiaire stagiaire, String nouveauNom) {
    	Stagiaire ancienStagiaire = stagiaire;
    	stagiaire.setNom(nouveauNom);
    	modifier(ancienStagiaire, stagiaire); 	
    }
    
    public static void modifierPrenom(Stagiaire stagiaire, String nouveauPrenom) {
    	Stagiaire ancienStagiaire = stagiaire;
    	stagiaire.setPrenom(nouveauPrenom);
    	modifier(ancienStagiaire, stagiaire); 	
    }
    
    public static void modifierDepartement(Stagiaire stagiaire, String nouveauDepartement) {
    	Stagiaire ancienStagiaire = stagiaire;
    	stagiaire.setDepartement(nouveauDepartement);
    	modifier(ancienStagiaire, stagiaire); 	
    }
    
    public static void modifierPromo(Stagiaire stagiaire, String nouvellePromo) {
    	Stagiaire ancienStagiaire = stagiaire;
    	stagiaire.setPromo(nouvellePromo);
    	modifier(ancienStagiaire, stagiaire); 	
    }
    
    public static void modifierAnneeEntree(Stagiaire stagiaire, String nouvelleAnneeEntree) {
    	Stagiaire ancienStagiaire = stagiaire;
    	stagiaire.setAnneeEntree(nouvelleAnneeEntree);
    	modifier(ancienStagiaire, stagiaire); 	
    }
    
    public static void modifier(Stagiaire ancienStagiaire, Stagiaire nouveauStagiaire) {
    	supprimer(ancienStagiaire);
    	ajouter(nouveauStagiaire);
    }
    
 // ******************* Création de la liste complète des stagiaire  *******************
	
 	/**
 	 * Méthode de création de la liste complète des stagiaires présent dans l'arbre
 	 * @param ArbreStagiaire : arbre
 	 * @return List<Stagiaire> : listInfixe
 	 */
 	public static List<Stagiaire> parcoursStagiaire(){
 		List<Stagiaire> listStagiaire = new ArrayList<Stagiaire>();
 		return parcoursStagiaire(racine, listStagiaire);
 	}
 	
 	private static List<Stagiaire> parcoursStagiaire(int r, List<Stagiaire> listInfixe){
 		if(r == -1) return listInfixe;
 		
 		parcoursStagiaire(lectureIndexFilsG(r), listInfixe);
 		listInfixe.add(lectureStagiaire(r));
 		parcoursStagiaire(lectureIndexFilsD(r), listInfixe);
 		
 		return listInfixe;
 	}
	@Override
	public String toString() {
		return racine + " ";
	}
}
