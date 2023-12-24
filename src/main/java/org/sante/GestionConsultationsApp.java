package org.sante;

import com.toedter.calendar.JDateChooser;
import org.sante.model.Medecin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GestionConsultationsApp {

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;

    public GestionConsultationsApp() throws SQLException {
        initialize();
        connectToDatabase();
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Gestion des Consultations Médicales");
        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());

        // Formulaire de connexion
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.add(new JLabel("Nom d'utilisateur:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Connexion");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Ajoutez la logique d'authentification ici
//                showMainMenu();
                performLogin();
            }
        });

        // Ajout du formulaire et du bouton à la frame
        frame.getContentPane().add(loginPanel);
        frame.getContentPane().add(loginButton);
    }

    private void connectToDatabase() throws SQLException {
        // Assurez-vous d'avoir la bibliothèque JDBC MySQL dans le chemin de classe de votre projet
        String url = "jdbc:mysql://localhost:3306/consultation";
        String user = "root";
        String password = "";

        connection = DriverManager.getConnection(url, user, password);
        createTables();
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        // Ajoutez ici vos requêtes de création de tables pour MySQL

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(255), prenom VARCHAR(255), login VARCHAR(255), password VARCHAR(255))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS medecins " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(255))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS patients " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(255))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS medicaments " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, libelle VARCHAR(255), indication VARCHAR(255), posologie VARCHAR(255))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS consultations " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, date DATE, " +
                "medecin_id INT, patient_id INT, " +
                "FOREIGN KEY(medecin_id) REFERENCES medecins(id), " +
                "FOREIGN KEY(patient_id) REFERENCES patients(id))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS prescriptions " +
                "(consultation_id INT, medicament_id INT, nb_jours INT," +
                "FOREIGN KEY(consultation_id) REFERENCES consultations(id), " +
                "FOREIGN KEY(medicament_id) REFERENCES medicaments(id))");

        statement.close();
    }

    private void performLogin() {
        String enteredUsername = usernameField.getText();
        char[] enteredPasswordChars = passwordField.getPassword();
        String enteredPassword = new String(enteredPasswordChars);

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM user WHERE login='" + enteredUsername + "' AND password='" + enteredPassword + "'";
            if (statement.executeQuery(query).next()) {
                JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                initializeMainMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Échec de la connexion. Veuillez vérifier vos informations.");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void initializeMainMenu() {
        frame.getContentPane().removeAll();
        frame.repaint();

        // Ajout de nouveaux boutons pour accéder aux différentes fonctionnalités
        JButton addMedecinButton = new JButton("Ajouter Médecin");
        JButton addPatientButton = new JButton("Ajouter Patient");
        JButton addMedicamentButton = new JButton("Ajouter Médicament");
        JButton addConsultationButton = new JButton("Enregistrer Consultation");
        JButton viewPatientListButton = new JButton("Liste des Patients");
        JButton viewConsultationHistoryButton = new JButton("Historique des Consultations");
//        JButton viewConsultationDetailsButton = new JButton("Détails Consultation");

        addMedecinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMedecinForm();
            }
        });

        addPatientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher le formulaire d'ajout de patient
                addPatientForm();
            }
        });

        addMedicamentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher le formulaire d'ajout de médicament
                addMedicamentForm();
            }
        });

        addConsultationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher le formulaire d'enregistrement de consultation
                addConsultationForm();
            }
        });

        viewPatientListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher la liste des patients
                viewPatientList();
            }
        });

        viewConsultationHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher l'historique des consultations d'un patient
                viewConsultationsList();
            }
        });

        /*
        viewConsultationDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logique pour afficher les détails d'une consultation sélectionnée
                // Vous pouvez appeler une méthode viewConsultationDetails() ici
            }
        });

         */

        // Ajout des boutons au menu principal avec un FlowLayout
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        frame.getContentPane().add(addMedecinButton);
        frame.getContentPane().add(addPatientButton);
        frame.getContentPane().add(addMedicamentButton);
        frame.getContentPane().add(addConsultationButton);
        frame.getContentPane().add(viewPatientListButton);
        frame.getContentPane().add(viewConsultationHistoryButton);
//        frame.getContentPane().add(viewConsultationDetailsButton);
    }

    private void addMedecinForm() {
        JTextField nomField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Nom du médecin:"));
        panel.add(nomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Ajouter Médecin", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String nom = nomField.getText();
            insertMedecin(nom);
            // Enregistrez les informations dans la base de données ou effectuez d'autres actions nécessaires
            JOptionPane.showMessageDialog(frame, "Médecin ajouté avec succès !");
        }
    }
    private void addPatientForm() {
        JTextField nomField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Nom du patient:"));
        panel.add(nomField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Ajouter Patient", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String nom = nomField.getText();
            insertPatient(nom);
            // Enregistrez les informations dans la base de données ou effectuez d'autres actions nécessaires
            JOptionPane.showMessageDialog(frame, "Patient ajouté avec succès !");
        }
    }
    private void addMedicamentForm() {
        JTextField libelleField = new JTextField();
        JTextField indicationField = new JTextField();
        JTextField posologieField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Libelle:"));
        panel.add(libelleField);
        panel.add(new JLabel("Indication:"));
        panel.add(indicationField);
        panel.add(new JLabel("Posologie:"));
        panel.add(posologieField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Ajouter Médicament", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String libelle = libelleField.getText();
            String indication = indicationField.getText();
            String posologie = posologieField.getText();

            insertMedicament(libelle, indication, posologie);

            // Enregistrez les informations dans la base de données ou effectuez d'autres actions nécessaires
            JOptionPane.showMessageDialog(frame, "Médicament ajouté avec succès !");
        }
    }
    private void addConsultationForm() {
        JDateChooser dateChooser = new JDateChooser();
        JComboBox<Medecin> medecinComboBox = new JComboBox<>();
        JTextField patientIdField = new JTextField();

        // Récupérer la liste des médecins depuis la base de données
        List<Medecin> medecins = getMedecinsFromDatabase();
        for (Medecin medecin : medecins) {
            medecinComboBox.addItem(medecin);
        }

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Date de consultation:"));
        panel.add(dateChooser);
        panel.add(new JLabel("Médecin"));
        panel.add(medecinComboBox);
        panel.add(new JLabel("N0 Patient:"));
        panel.add(patientIdField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enregistrer Consultation", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {

            // Convertissez java.util.Date en java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(dateChooser.getDate().getTime());

            Medecin selectedMedecin = (Medecin) medecinComboBox.getSelectedItem();
            int medecinId = selectedMedecin.getId();
            int patientId = Integer.parseInt(patientIdField.getText());

            insertConsultation(sqlDate, medecinId, patientId);

            // Enregistrez les informations dans la base de données ou effectuez d'autres actions nécessaires
            JOptionPane.showMessageDialog(frame, "Consultation enregistrée avec succès !");
        }
    }

    ////////////////////////
    private void viewPatientList() {
        // Logique pour récupérer la liste des patients depuis la base de données
        List<String> patients = getPatientsFromDatabase();

        // Création des colonnes du tableau
        String[] columnNames = {"ID du patient", "Nom du patient"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Ajout des données des patients au modèle du tableau
        for (String patient : patients) {
            String[] patientInfo = patient.split(", ");
            model.addRow(new Object[]{Integer.parseInt(patientInfo[0]), patientInfo[1]});
        }

        // Création du tableau et ajout dans un JScrollPane
        JTable patientTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        // Affichage du tableau dans une boîte de dialogue
        JOptionPane.showMessageDialog(frame, scrollPane, "Liste des Patients", JOptionPane.PLAIN_MESSAGE);
    }
    private void viewConsultationsList() {
        // Logique pour récupérer la liste des consultations depuis la base de données
        List<String> consultations = getConsultationsFromDatabase();

        // Création des colonnes du tableau
        String[] columnNames = {"ID de la consultation", "Date de la consultation", "Nom du patient", "Nom du médecin"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Ajout des données des consultations au modèle du tableau
        for (String consultation : consultations) {
            // Split la chaîne pour obtenir les informations
            String[] consultationInfo = consultation.split(", ");

            // Utiliser une expression régulière pour extraire uniquement les chiffres de l'ID
            String idStr = consultationInfo[0].trim().replaceAll("\\D+", "");

            // Assurez-vous que l'ID n'est pas vide avant de le convertir en entier
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);

                // Vérifier si le tableau a suffisamment d'éléments avant d'essayer d'accéder à l'indice 3
                if (consultationInfo.length >= 4) {
                    model.addRow(new Object[]{id, consultationInfo[1], consultationInfo[2], consultationInfo[3]});
                } else {
                    // Si le tableau n'a pas suffisamment d'éléments, ajoutez des valeurs par défaut
                    model.addRow(new Object[]{id, consultationInfo[1], "", ""});
                }
            }
        }



        // Création du tableau et ajout dans un JScrollPane
        JTable consultationTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(consultationTable);

        // Affichage du tableau dans une boîte de dialogue
        JOptionPane.showMessageDialog(frame, scrollPane, "Liste des Consultations", JOptionPane.PLAIN_MESSAGE);
    }


    //////////////////////////////////////////////////////
    public void insertUser(String nom, String prenom, String login, String password) {
        try {
            String query = "INSERT INTO user (nom, prenom, login, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nom);
                preparedStatement.setString(2, prenom);
                preparedStatement.setString(3, login);
                preparedStatement.setString(4, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertMedecin(String nom) {
        try {
            String query = "INSERT INTO medecins (nom) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nom);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertPatient(String nom) {
        try {
            String query = "INSERT INTO patients (nom) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nom);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertMedicament(String libelle, String indication, String posologie) {
        try {
            String query = "INSERT INTO medicaments (libelle, indication, posologie) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, libelle);
                preparedStatement.setString(2, indication);
                preparedStatement.setString(3, posologie);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertConsultation(Date date, int medecinId, int patientId) {
        try {
            String query = "INSERT INTO consultations (date, medecin_id, patient_id) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setInt(2, medecinId);
                preparedStatement.setInt(3, patientId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////
    // Méthode pour récupérer la liste des médecins depuis la base de données
    private List<Medecin> getMedecinsFromDatabase() {
        List<Medecin> medecins = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT id, nom FROM medecins";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int medecinId = resultSet.getInt("id");
                String medecinNom = resultSet.getString("nom");
                Medecin medecin = new Medecin(medecinId, medecinNom);
                medecins.add(medecin);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medecins;
    }
    // Méthode pour récupérer la liste des patients depuis la base de données
    private List<String> getPatientsFromDatabase() {
        List<String> patients = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, nom FROM patients");

            while (resultSet.next()) {
                int patientId = resultSet.getInt("id");
                String patientName = resultSet.getString("nom");
                String patientInfo = patientId + ", " + patientName;
                patients.add(patientInfo);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }
    // Méthode pour récupérer la liste des consultations depuis la base de données
    private List<String> getConsultationsFromDatabase() {
        List<String> consultations = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT c.id, c.date, p.nom AS patient_nom, m.nom AS medecin_nom " +
                    "FROM consultations c " +
                    "JOIN patients p ON c.patient_id = p.id " +
                    "JOIN medecins m ON c.medecin_id = m.id");
            while (resultSet.next()) {
                Date date = resultSet.getDate("date");
                String patientNom = resultSet.getString("patient_nom");
                String medecinNom = resultSet.getString("medecin_nom");
                int idConsultation = resultSet.getInt(("id"));

                consultations.add(idConsultation +", " + date.toString() + ", " + patientNom + ", " + medecinNom);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }




}
