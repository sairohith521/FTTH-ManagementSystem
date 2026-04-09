package ftth.repository;

import ftth.model.OLT;
import java.io.*;
import java.util.*;

public class InventoryRepository {

    private static final String FILE_PATH = "data/inventory.txt";

    public List<OLT> loadAll() {
        List<OLT> list = new ArrayList<>();

        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {

                String[] d = line.split(",");
                for (int i = 0; i < d.length; i++) d[i] = d[i].trim();

                list.add(new OLT(
                        d[0],
                        d[1],
                        d[2],
                        Integer.parseInt(d[3]),
                        Boolean.parseBoolean(d[4])
                ));
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveAll(List<OLT> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (OLT o : list) {
                bw.write(o.getOltId() + "," + o.getPincode() + "," +
                        o.getType() + "," + o.getSplitterCount() + "," +
                        o.isHasCustomer());
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}