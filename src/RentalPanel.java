import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class RentalPanel extends JPanel {
    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;
    int id = -1;

    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();

    JLabel personL = new JLabel("Клиент:");
    JLabel carL = new JLabel("Автомобил:");
    JLabel rentalDaysL = new JLabel("Дни наем:");
    JLabel priceL = new JLabel("Цена:");

    JTextField personTF = new JTextField();
    JTextField carTF = new JTextField();
    JTextField rentalDaysTF = new JTextField();
    JTextField priceTF = new JTextField();

    JButton addBTN = new JButton("Добавяне");
    JButton deleteBTN = new JButton("Изтриване");
    JButton editBTN = new JButton("Промяна");
    JButton searchBTN = new JButton("Търсене по дни");
    JButton searchByClientCarBTN = new JButton("Търсене по клиент и автомобил");
    JButton refreshBTN = new JButton("Обнови");

    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);

    public RentalPanel() {
        this.setLayout(new GridLayout(3, 1));

        // Upper Panel
        upPanel.setLayout(new GridLayout(5, 2));
        upPanel.setBackground(new Color(245, 245, 220));
        upPanel.add(personL);
        upPanel.add(personTF);
        upPanel.add(carL);
        upPanel.add(carTF);
        upPanel.add(rentalDaysL);
        upPanel.add(rentalDaysTF);
        upPanel.add(priceL);
        upPanel.add(priceTF);
        this.add(upPanel);

        // Middle Panel
        midPanel.setBackground(new Color(245, 245, 220));
        midPanel.add(addBTN);
        midPanel.add(deleteBTN);
        midPanel.add(editBTN);
        midPanel.add(searchBTN);
        midPanel.add(searchByClientCarBTN);
        midPanel.add(refreshBTN);

        this.add(midPanel);

        // Lower Panel
        myScroll.setPreferredSize(new Dimension(350, 150));
        downPanel.setBackground(new Color(245, 245, 220));
        downPanel.add(myScroll);
        this.add(downPanel);
        table.addMouseListener(new MouseAction());

        table.addMouseListener(new MouseAction());

        addBTN.addActionListener(new AddAction());
        deleteBTN.addActionListener(new DeleteAction());
        editBTN.addActionListener(new EditAction());
        searchBTN.addActionListener(new SearchAction());
        searchByClientCarBTN.addActionListener(new SearchByClientCarAction());
        refreshBTN.addActionListener(new RefreshAction());

        refreshTable();
        this.setVisible(true);
    }

    public void refreshTable() {
        conn = DBConnection.getConnection();
        String sql = "SELECT r.RENTAL_ID, p.fname AS Person_Name, p.lname AS Person_Surname, " +
                "CONCAT(c.make, ' ', c.model) AS Car_Info, r.RENTAL_DAYS, r.PRICE " +
                "FROM Rental r " +
                "JOIN Person p ON r.PERSON_ID = p.ID " +
                "JOIN Car c ON r.CAR_ID = c.CAR_ID";
        try {
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            table.setModel(new MyModel(result));

            // Скриваме колоната с RENTAL_ID (колона 0)
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void clearForm() {
        rentalDaysTF.setText("");
        priceTF.setText("");
        personTF.setText("");
        carTF.setText("");
    }

    private Integer getPersonIdByName(String fullName) {
        conn = DBConnection.getConnection();
        String sql = "SELECT ID FROM Person WHERE CONCAT(fname, ' ', lname) = ?";
        try {
            state = conn.prepareStatement(sql);
            state.setString(1, fullName);
            result = state.executeQuery();
            if (result.next()) {
                return result.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getCarIdByInfo(String carInfo) {
        conn = DBConnection.getConnection();
        String sql = "SELECT CAR_ID FROM Car WHERE CONCAT(make, ' ', model) = ?";
        try {
            state = conn.prepareStatement(sql);
            state.setString(1, carInfo);
            result = state.executeQuery();
            if (result.next()) {
                return result.getInt("CAR_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Integer personId = getPersonIdByName(personTF.getText().trim());
            Integer carId = getCarIdByInfo(carTF.getText().trim());

            if (personId == null) {
                JOptionPane.showMessageDialog(null, "Не съществува такъв клиент");
                return;
            }
            if (carId == null) {
                JOptionPane.showMessageDialog(null, "Не съществува такъв автомобил");
                return;
            }

            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Rental (PERSON_ID, CAR_ID, RENTAL_DAYS, PRICE) VALUES (?, ?, ?, ?)";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, personId);
                state.setInt(2, carId);
                state.setInt(3, Integer.parseInt(rentalDaysTF.getText()));
                state.setFloat(4, Float.parseFloat(priceTF.getText()));
                state.execute();
                refreshTable();
                clearForm();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class SearchByClientCarAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.RENTAL_ID, p.fname AS Person_Name, p.lname AS Person_Surname, " +
                    "CONCAT(c.make, ' ', c.model) AS Car_Info, r.RENTAL_DAYS, r.PRICE " +
                    "FROM Rental r " +
                    "JOIN Person p ON r.PERSON_ID = p.ID " +
                    "JOIN Car c ON r.CAR_ID = c.CAR_ID " +
                    "WHERE CONCAT(p.fname, ' ', p.lname) = ? AND CONCAT(c.make, ' ', c.model) = ?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, personTF.getText().trim());
                state.setString(2, carTF.getText().trim());
                result = state.executeQuery();
                try {
                    table.setModel(new MyModel(result));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                // Скриваме ID колоната и тук
                table.getColumnModel().getColumn(0).setMinWidth(0);
                table.getColumnModel().getColumn(0).setMaxWidth(0);
                table.getColumnModel().getColumn(0).setWidth(0);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    class DeleteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (id == -1) return;
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Rental WHERE RENTAL_ID=?";
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

    class EditAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (id == -1) return;

            Integer personId = getPersonIdByName(personTF.getText().trim());
            Integer carId = getCarIdByInfo(carTF.getText().trim());

            if (personId == null) {
                JOptionPane.showMessageDialog(null, "Не съществува такъв клиент");
                return;
            }
            if (carId == null) {
                JOptionPane.showMessageDialog(null, "Не съществува такъв автомобил");
                return;
            }

            conn = DBConnection.getConnection();
            String sql = "UPDATE Rental SET PERSON_ID=?, CAR_ID=?, RENTAL_DAYS=?, PRICE=? WHERE RENTAL_ID=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, personId);
                state.setInt(2, carId);
                state.setInt(3, Integer.parseInt(rentalDaysTF.getText()));
                state.setFloat(4, Float.parseFloat(priceTF.getText()));
                state.setInt(5, id);
                state.executeUpdate();
                refreshTable();
                clearForm();
                id = -1;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.RENTAL_ID, p.fname AS Person_Name, p.lname AS Person_Surname, " +
                    "CONCAT(c.make, ' ', c.model) AS Car_Info, r.RENTAL_DAYS, r.PRICE " +
                    "FROM Rental r " +
                    "JOIN Person p ON r.PERSON_ID = p.ID " +
                    "JOIN Car c ON r.CAR_ID = c.CAR_ID WHERE r.RENTAL_DAYS=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, Integer.parseInt(rentalDaysTF.getText()));
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


    class RefreshAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTable();
            clearForm();
        }
    }

    class MouseAction implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            id = Integer.parseInt(table.getValueAt(row, 0).toString());
            personTF.setText(table.getValueAt(row, 1).toString() + " " + table.getValueAt(row, 2).toString());
            carTF.setText(table.getValueAt(row, 3).toString()); // вече съдържа make + model
            rentalDaysTF.setText(table.getValueAt(row, 4).toString());
            priceTF.setText(table.getValueAt(row, 5).toString());
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

}
