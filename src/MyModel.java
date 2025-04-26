import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel {

    // Сериализационен UID за съвместимост с версията
    private static final long serialVersionUID = 1L;

    // Променливи за съхранение на данни от ResultSet и броя на редовете и колоните
    private ResultSet result;
    private int rowCount;
    private int columnCount;
    private ArrayList<Object> data = new ArrayList<Object>();

    // Конструктор, който инициализира модела с резултати от запитване
    public MyModel(ResultSet rs) throws Exception {
        setRS(rs);
    } // end constructor

    // Метод за задаване на ResultSet и попълване на ArrayList с данни
    public void setRS(ResultSet rs) throws Exception {
        this.result = rs;
        ResultSetMetaData metaData = rs.getMetaData();
        rowCount = 0;
        columnCount = metaData.getColumnCount();

        // Попълване на ArrayList с редове от ResultSet
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int j = 0; j < columnCount; j++) {
                row[j] = rs.getObject(j + 1);  // Взимане на данни за всяка колона
            }
            data.add(row);  // Добавяне на ред в ArrayList
            rowCount++;  // Увеличаване на броя на редовете
        } // while
    } // end setRS

    // Метод за връщане на броя на колоните
    public int getColumnCount() {
        return columnCount;
    }

    // Метод за връщане на броя на редовете
    public int getRowCount() {
        return rowCount;
    }

    // Метод за връщане на стойността на дадена клетка по индекс
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = (Object[]) data.get(rowIndex);  // Вземане на ред по индекс
        return row[columnIndex];  // Връщане на стойността на съответната клетка
    }

    // Метод за получаване на името на колоната по индекс
    public String getColumnName(int columnIndex) {
        try {
            ResultSetMetaData metaData = result.getMetaData();  // Вземане на метаданни от ResultSet
            return metaData.getColumnName(columnIndex + 1);  // Връщане на името на колоната
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // В случай на грешка, връща null
        }
    } // end getColumnName
} // end class MyModel
