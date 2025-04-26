import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class MyFrame extends JFrame{
    Connection conn=null;
    PreparedStatement state=null;
    ResultSet result=null;
    int id=-1;

    JPanel upPanel=new JPanel();
    JPanel midPanel=new JPanel();
    JPanel downPanel=new JPanel();

    JLabel fnameL=new JLabel("Име:");
    JLabel lnameL=new JLabel("Фамилия:");
    JLabel sexL=new JLabel("Пол:");
    JLabel ageL=new JLabel("Години:");
    JLabel salaryL=new JLabel("Заплата:");

    JTextField fnameTF=new JTextField();
    JTextField lnameTF=new JTextField();
    JTextField ageTF=new JTextField();
    JTextField salaryTF=new JTextField();

    String[] item= {"Мъж","Жена"};
    JComboBox<String> sexCombo=new JComboBox<String>(item);
    JComboBox<String> personCombo = new JComboBox<>();

    JButton addBTN=new JButton("Добавяне");
    JButton deleteBTN=new JButton("Изтриване");
    JButton editBTN=new JButton("Промяна");
    JButton searchBTN=new JButton("Търсене по години");
    JButton searchBySalaryBTN = new JButton("Търсене по заплата");
    JButton refreshBTN=new JButton("Обнови");

    JTable table=new JTable();
    JScrollPane myScroll=new JScrollPane(table);



    public void refreshTable() {
        conn = DBConnection.getConnection();
        try {
            state = conn.prepareStatement("select * from person");
            result = state.executeQuery();
            table.setModel(new MyModel(result));

            // Скриване на колоната с ID
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyFrame() {
        this.setSize(400, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 1));

        // Променяме фона на целия JFrame
        this.getContentPane().setBackground(new Color(245, 245, 220));


        // upPanel-------------------------------------
        upPanel.setLayout(new GridLayout(5, 2));
        upPanel.setBackground(new Color(245, 245, 220));
        upPanel.add(fnameL);
        upPanel.add(fnameTF);
        upPanel.add(lnameL);
        upPanel.add(lnameTF);
        upPanel.add(sexL);
        upPanel.add(sexCombo);
        upPanel.add(ageL);
        upPanel.add(ageTF);
        upPanel.add(salaryL);
        upPanel.add(salaryTF);

        this.add(upPanel);

        // midPanel------------------------------------
        midPanel.setBackground(new Color(245, 245, 220));
        midPanel.add(addBTN);
        midPanel.add(deleteBTN);
        midPanel.add(editBTN);
        midPanel.add(searchBTN);
        midPanel.add(refreshBTN);
        midPanel.add(personCombo);

        this.add(midPanel);

        // Свързване на бутоните с действия
        addBTN.addActionListener(new AddAction());
        deleteBTN.addActionListener(new DeleteAction());
        searchBTN.addActionListener(new SearchAction());
        refreshBTN.addActionListener(new RefreshAction());
        editBTN.addActionListener(new EditAction());
        searchBySalaryBTN.addActionListener(new SearchBySalaryAction());


        // downPanel----------------------------------
        downPanel.setBackground(new Color(245, 245, 220));
        myScroll.setPreferredSize(new Dimension(350, 150));
        downPanel.add(myScroll);
        this.add(downPanel);
        table.addMouseListener(new MouseAction());

        // Обновяваме изгледа след промени на фона и бутоните
        this.revalidate();
        this.repaint();

        refreshTable();
        refreshPersonCombo();
        this.setVisible(true);
    }



    public void refreshPersonCombo() {
        personCombo.removeAllItems();
        conn = DBConnection.getConnection();
        String sql = "select id, fname, lname from person";
        try {
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            while (result.next()) {
                String fullName = result.getString("fname") + " " + result.getString("lname");
                personCombo.addItem(fullName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void clearForm() {
        fnameTF.setText("");
        lnameTF.setText("");
        ageTF.setText("");
        salaryTF.setText("");
    }

    class AddAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="insert into person (fname, lname, sex, age, salary) values (?,?,?,?,?)";
            try {
                state=conn.prepareStatement(sql);
                state.setString(1, fnameTF.getText());
                state.setString(2, lnameTF.getText());
                state.setString(3, sexCombo.getSelectedItem().toString());
                state.setInt(4, Integer.parseInt(ageTF.getText()));
                state.setFloat(5, Float.parseFloat(salaryTF.getText()));
                state.execute();
                refreshTable();
                refreshPersonCombo();
                clearForm();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


        }

    }

    class MouseAction implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {

            int row=table.getSelectedRow();
            id=Integer.parseInt(table.getValueAt(row, 0).toString());
            fnameTF.setText(table.getValueAt(row, 1).toString());
            lnameTF.setText(table.getValueAt(row, 2).toString());
            ageTF.setText(table.getValueAt(row, 4).toString());
            salaryTF.setText(table.getValueAt(row, 5).toString());
            if(table.getValueAt(row, 3).toString().equals("Мъж")) {
                sexCombo.setSelectedIndex(0);
            }
            else {
                sexCombo.setSelectedIndex(1);
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

    }

    class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="delete from person where id=?";
            try {
                state=conn.prepareStatement(sql);
                state.setInt(1, id);
                state.execute();
                refreshTable();
                refreshPersonCombo();
                clearForm();
                id=-1;
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }

    class SearchAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="select * from person where age=?";
            try {
                state=conn.prepareStatement(sql);
                state.setInt(1, Integer.parseInt(ageTF.getText()));
                result=state.executeQuery();
                table.setModel(new MyModel(result));
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }

    class RefreshAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            refreshTable();
            clearForm();

        }

    }

    class EditAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (id == -1) {
                return; // ако не е избран запис
            }

            conn = DBConnection.getConnection();
            String sql = "UPDATE person SET fname=?, lname=?, sex=?, age=?, salary=? WHERE id=?";

            try {
                state = conn.prepareStatement(sql);
                state.setString(1, fnameTF.getText());
                state.setString(2, lnameTF.getText());
                state.setString(3, sexCombo.getSelectedItem().toString());
                state.setInt(4, Integer.parseInt(ageTF.getText()));
                state.setFloat(5, Float.parseFloat(salaryTF.getText()));
                state.setInt(6, id);
                state.execute();

                refreshTable();
                refreshPersonCombo();
                clearForm();
                id = -1; // нулираме избрания ID

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    class SearchBySalaryAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM person WHERE salary=?";
            try {
                state = conn.prepareStatement(sql);
                state.setFloat(1, Float.parseFloat(salaryTF.getText()));
                result = state.executeQuery();
                try {
                    table.setModel(new MyModel(result));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }



}