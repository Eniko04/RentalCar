import javax.swing.*;
import java.awt.*;

public class NewMyFrame extends JFrame {

    JPanel personP = new JPanel();  // Тук използваме JPanel
    JPanel carP = new CarPanel();
    JPanel rentaP = new RentalPanel();


    JTabbedPane tabbedPane = new JTabbedPane();

    public NewMyFrame() {
        this.setSize(400, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Добавяме съдържанието на MyFrame в personP
        MyFrame myFrame = new MyFrame(); // Създаваме обект от MyFrame
        personP.setLayout(new BoxLayout(personP, BoxLayout.Y_AXIS));  // Можеш да зададеш друг layout
        personP.add(myFrame.upPanel);
        personP.add(myFrame.midPanel);
        personP.add(myFrame.downPanel);

        // Добавяне на панелите към таба
        tabbedPane.add(personP,"Клиенти");
        tabbedPane.add(carP,"Коли");
        tabbedPane.add(rentaP,"Наем");

        this.add(tabbedPane);
        this.setVisible(true);
    }
}
