package org.sante.model;

public class Medecin {
    private int id;
    private String nom;

    public Medecin(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom; // Utilisé par JComboBox pour afficher le nom dans la liste déroulante
    }
}
