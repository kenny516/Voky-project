package com.mg.vokye.function;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mg.vokye.bdd.Connexion;
import com.mg.vokye.model.Produit;


// responsable clientele
public class Function {
    public static String check_login(String nom, String prenom, String password) throws Exception {
        Connexion pgConnect = new Connexion();
        Connection connection = pgConnect.getConnection();
        String designation = null;
        
        String query = "SELECT designation FROM vue_employe_type WHERE nom = ? AND prenom = ? AND motDePasse = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, password);           
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // L'utilisateur existe, récupérer la désignation
                    designation = rs.getString("Designation");
                }
            }
        } catch (SQLException e) {
            // Gérez les exceptions SQL ici
            e.printStackTrace();
            throw new Exception("Database query error", e);
        } finally {
            // Fermez la connexion à la base de données
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return designation;
    }
    

    public static int getNombreProduit() throws Exception {
        Connexion pgConnect = new Connexion();
        int count = 0;
        Connection connection = pgConnect.getConnection();
    
        String sql = "select count(*) from produit";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
    
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
    
        // Fermer les ressources
        resultSet.close();
        preparedStatement.close();
        connection.close();
    
        return count;
    }
    

    public static List<Produit> getListProduit() throws Exception {
        int k = Function.getNombreProduit();
        List<Produit> listProduit = new ArrayList<>();
        Connexion pgConnect = new Connexion();
        Connection connection = pgConnect.getConnection();
        
        String sql = "select * from produit";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        int index = 0;
        while(resultSet.next() && index < 5) {
            Produit produit = new Produit();
            // Supposons que votre classe Produit ait des méthodes setters pour définir ses propriétés
            produit.setIdProduit(resultSet.getInt("id_produit"));
            produit.setNomProduit(resultSet.getString("nom"));
            produit.setPrix(resultSet.getDouble("prix"));
            // et ainsi de suite pour d'autres propriétés
            
            listProduit.add(produit); // Ajout de l'objet Produit à la liste
            index++;
        }
        
        // Fermer les ressources
        resultSet.close();
        preparedStatement.close();
        connection.close();
        
        return listProduit;
    }
    
    //Function Chris : chiffre affaire 

    //// function pour depense
    public double total_depense(Connection connex, Date d1, Date d2) throws Exception {
        // Initialize variables
        Date date_first, date_last;
        double total = 0;

        // Swap dates if necessary to ensure date_first is earlier than date_last
        if (d1.compareTo(d2) > 0) {
            date_first = d2;
            date_last = d1;
        } else {
            date_first = d1;
            date_last = d2;
        }

        // Use PreparedStatement to prevent SQL injection and handle date parameters
        String sql = "SELECT id_prix FROM depense WHERE date_depense > ? AND date_depense < ?";

        try (PreparedStatement stmt = connex.prepareStatement(sql)) {
            stmt.setDate(1, date_first);
            stmt.setDate(2, date_last);

            try (ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    total += res.getDouble("id_prix");
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return total;
    }

    //// Function pour vente
    public double total_vente(Connection connex, Date d1, Date d2) throws Exception {
        // Initialize variables
        Date date_first, date_last;
        double total = 0;

        // Swap dates if necessary to ensure date_first is earlier than date_last
        if (d1.compareTo(d2) > 0) {
            date_first = d2;
            date_last = d1;
        } else {
            date_first = d1;
            date_last = d2;
        }

        // Use PreparedStatement to prevent SQL injection and handle date parameters
        // properly
        String sql = "SELECT id_produit, quantite FROM vente WHERE date_vente > ? AND date_vente < ?";

        try (PreparedStatement stmt = connex.prepareStatement(sql)) {
            stmt.setDate(1, date_first);
            stmt.setDate(2, date_last);

            try (ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    double prixProduit = getPrixProduit(res.getDouble("id_produit"));
                    int quantite = res.getInt("quantite");
                    total += prixProduit * quantite;
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return total;
    }

    public double getPrixProduit(Connection connex, int id_produit) throws Exception {
        Produit pd = new Produit();
        try{
        pd.getById(connex, id_produit);
        }catch(Exception e){
            throw e;
        }
        return pd.getPrix();
    }

    //// Function chiffre affaire
    public double chiffre_affaire(Connection connex, Date d1, Date d2) throws Exception {
        double chiffre_affaire = 0;
        try {
            chiffre_affaire = this.total_depense(connex, d1, d2) - this.total_vente(connex, d1, d2);
        } catch (Exception e) {
            throw e;
        }
        return chiffre_affaire;
    }
}