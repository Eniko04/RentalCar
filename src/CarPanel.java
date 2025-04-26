// Импортиране на нужните библиотеки
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class CarPanel extends JPanel {
    // Променливи за работа с базата данни
    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;
    int id = -1; // Използва се за идентификация на избран запис

    // Панели за подреждане на UI компонентите
    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();

    // Етикети
    JLabel makeL = new JLabel("Марка:");
    JLabel modelL = new JLabel("Модел:");
    JLabel categoryL = new JLabel("Категория:");
    JLabel yearL = new JLabel("Година:");
    JLabel transmissionL = new JLabel("Трансмисия:");

    // Текстови полета за въвеждане на данни
    JTextField makeTF = new JTextField();
    JTextField modelTF = new JTextField();
    JTextField categoryTF = new JTextField();
    JTextField yearTF = new JTextField();

    // Падащо меню за избор на трансмисия
    String[] transmissions = {"Manual", "Automatic", "Semi-automatic"};
    JComboBox<String> transmissionCombo = new JComboBox<>(transmissions);

    // Бутони за действия
    JButton addBTN = new JButton("Добавяне");
    JButton deleteBTN = new JButton("Изтриване");
    JButton editBTN = new JButton("Промяна");
    JButton searchBTN = new JButton("Търсене по години");
    JButton refreshBTN = new JButton("Обнови");

    // Таблица за показване на данните и скрол бар
    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);

    // Конструктор - настройва интерфейса
    public CarPanel() {
        this.setSize(400, 600);
        this.setLayout(new GridLayout(3, 1));



        // Горен панел с входни полета
        upPanel.setLayout(new GridLayout(5, 2));
        upPanel.setBackground(new Color(245, 245, 220));  // Променяме фона на upPanel
        upPanel.add(makeL);
        upPanel.add(makeTF);
        upPanel.add(modelL);
        upPanel.add(modelTF);
        upPanel.add(categoryL);
        upPanel.add(categoryTF);
        upPanel.add(yearL);
        upPanel.add(yearTF);
        upPanel.add(transmissionL);
        upPanel.add(transmissionCombo);
        this.add(upPanel);

        // Среден панел с бутони
        midPanel.setBackground(new Color(245, 245, 220)); // Променяме фона на midPanel
        midPanel.add(addBTN);
        midPanel.add(deleteBTN);
        midPanel.add(editBTN);
        midPanel.add(searchBTN);
        midPanel.add(refreshBTN);
        this.add(midPanel);

        // Свързване на бутоните с действия
        addBTN.addActionListener(new AddAction());
        deleteBTN.addActionListener(new DeleteAction());
        editBTN.addActionListener(new EditAction());
        searchBTN.addActionListener(new SearchAction());
        refreshBTN.addActionListener(new RefreshAction());

        // Долен панел с таблицата
        myScroll.setPreferredSize(new Dimension(350, 150));
        downPanel.setBackground(new Color(245, 245, 220));
        downPanel.add(myScroll);
        this.add(downPanel);
        table.addMouseListener(new MouseAction()); // Добавяне на слушател за клик

        // Зареждане на таблицата с данни
        refreshTable();

        this.setVisible(true);
    }


    // Метод за презареждане на данните в таблицата
    public void refreshTable() {
        conn = DBConnection.getConnection();
        try {
            state = conn.prepareStatement("SELECT * FROM Car");
            result = state.executeQuery();
            try {
                table.setModel(new MyModel(result));
                // Скриваме колоната с ID (car_id), за да не се вижда от потребителя
                table.getColumnModel().getColumn(0).setMinWidth(0);
                table.getColumnModel().getColumn(0).setMaxWidth(0);
                table.getColumnModel().getColumn(0).setWidth(0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Грешка при зареждане на таблицата: " + e.getMessage());
        }
    }

    // Метод за изчистване на формата
    public void clearForm() {
        makeTF.setText("");
        modelTF.setText("");
        categoryTF.setText("");
        yearTF.setText("");
        transmissionCombo.setSelectedIndex(0);
    }

    // Действие при натискане на бутона "Добавяне"
    class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO car (make, model, category, production_year, transmission) VALUES (?, ?, ?, ?, ?)";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, makeTF.getText());
                state.setString(2, modelTF.getText());
                state.setString(3, categoryTF.getText());
                state.setInt(4, Integer.parseInt(yearTF.getText()));
                state.setString(5, transmissionCombo.getSelectedItem().toString());
                state.execute();
                refreshTable(); // Обновяване на таблицата
                clearForm();    // Изчистване на формата
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Действие при натискане на бутона "Изтриване"
    class DeleteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (id == -1) return; // Ако не е избран запис
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM car WHERE car_id=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, id);
                state.execute();
                refreshTable();
                clearForm();
                id = -1;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Действие при натискане на бутона "Промяна"
    class EditAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (id == -1) return;
            conn = DBConnection.getConnection();
            String sql = "UPDATE car SET make=?, model=?, category=?, production_year=?, transmission=? WHERE car_id=?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, makeTF.getText());
                state.setString(2, modelTF.getText());
                state.setString(3, categoryTF.getText());
                state.setInt(4, Integer.parseInt(yearTF.getText()));
                state.setString(5, transmissionCombo.getSelectedItem().toString());
                state.setInt(6, id);
                state.executeUpdate();
                refreshTable();
                clearForm();
                id = -1;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Действие при натискане на бутона "Търсене по години"
    class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM car WHERE production_year=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, Integer.parseInt(yearTF.getText()));
                result = state.executeQuery();
                try {
                    table.setModel(new MyModel(result));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Действие при натискане на бутона "Обнови"
    class RefreshAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTable();
            clearForm();
        }
    }

    // Слушател за събития с мишката върху таблицата
    class MouseAction implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow(); // Взимаме избрания ред
            id = Integer.parseInt(table.getValueAt(row, 0).toString()); // ID от скритата колона
            makeTF.setText(table.getValueAt(row, 1).toString());
            modelTF.setText(table.getValueAt(row, 2).toString());
            categoryTF.setText(table.getValueAt(row, 3).toString());
            yearTF.setText(table.getValueAt(row, 4).toString());

            // Избиране на правилната трансмисия
            String transmission = table.getValueAt(row, 5).toString();
            for (int i = 0; i < transmissions.length; i++) {
                if (transmissions[i].equals(transmission)) {
                    transmissionCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Други методи от MouseListener (не използвани)
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
}
