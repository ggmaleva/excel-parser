package ru.siblion.excel.parser.excelparser;

import java.nio.file.Paths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.siblion.excel.parser.excelparser.core.ExcelToXMLParser;
import ru.siblion.excel.parser.excelparser.core.Parser;
import ru.siblion.excel.parser.excelparser.core.Report;

@SpringBootApplication
public class ExcelParserApplication {
    public static void main(String[] args)  {
        SpringApplication.run(ExcelParserApplication.class, args);
        try {
            if (args.length < 2) {
                throw new Exception("You must fill args : [0] - repository folder , [1] - excel file");
            }
            Parser parser = new ExcelToXMLParser(Paths.get(args[1]), Paths.get(args[0]));
            Report report = parser.parse();
            report.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
