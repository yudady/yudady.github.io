package tk.tommy;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {

        Map<String, String> lowerCaseKeyMap = new HashMap<>();


        File customersCsv = new File("C:\\Users\\tommy.lin\\Desktop\\data\\cname.csv");
        File conflict_names_File = new File("C:\\Users\\tommy.lin\\Desktop\\data\\conflict_names.csv");

        Files.readLines(customersCsv, Charset.defaultCharset()).forEach(row -> {
            String[] cells = row.split(",");
            String normalName = cells[1].replaceAll("\"", "");
            String lowerName = cells[2].replaceAll("\"", "");

            lowerCaseKeyMap.put(lowerName, normalName);

        });

        List<String> csvList = new ArrayList<>();
        csvList.add("\"lower_case_customer_name\",\"pf2_db_customer_name\"");

        Files.readLines(conflict_names_File, Charset.defaultCharset()).forEach(conflictLowerCaseName -> {

            csvList.add("\"" + conflictLowerCaseName + "\",\"" + lowerCaseKeyMap.get(conflictLowerCaseName) + "\"");
        });


        System.out.println("csvList.size() = " + csvList.size());

        FileWriter writer = new FileWriter("C:\\Users\\tommy.lin\\Desktop\\data\\conflict_customer_names.csv");
        for (String str : csvList) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();

    }
}
