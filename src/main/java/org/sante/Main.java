package org.sante;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GestionConsultationsApp();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}