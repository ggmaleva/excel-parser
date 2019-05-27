package ru.siblion.excel.parser.excelparser.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;
import ru.siblion.wsdl.parser.WsdlParser;

public class ImporterExcel implements Importer {

    private Map<String, Set<String>> moduleWsdl;
    private Set<String> endpoints;
    private Path source;
    private Path scanPath;
    private ModuleMethodContent content;

    public ImporterExcel(Path source, Path scanPath) {
        moduleWsdl = new HashMap<>();
        endpoints = new HashSet<>();
        content = new ModuleMethodContent();
        this.scanPath = scanPath;
        this.source = source;
    }

    public Content getContent() {
        return content;
    }

    public void putContent() throws Exception{
        readModuleWsdlRows(readExcelSheet("Module-Endpoint"));
        readEndpointRows(readExcelSheet("Endpoint"));
        buildContent();
    }

    private  Iterator<Row> readExcelSheet(String sheetName) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(Files.newInputStream(source))) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            return sheet.iterator();
        }
    }

    private void readModuleWsdlRows (Iterator<Row> rowIterator)  {
        rowIterator.forEachRemaining(this::filterModuleWsdlRow);
    }

    private void readEndpointRows (Iterator<Row> rowIterator)  {
        rowIterator.forEachRemaining(this::filterEndpointRow);
    }

    private void filterModuleWsdlRow (Row row) {
        if (row.getCell(0).getCellType() == CellType.STRING &&
                row.getCell(1).getCellType() == CellType.STRING &&
                row.getCell(1).getStringCellValue().contains("META-INF")) {
            String module = row.getCell(0).getStringCellValue();
            String moduleName = module.contains("-") ? module.substring(0, module.indexOf("-")) : module;
            String wsdlPath = row.getCell(1).getStringCellValue().replaceFirst("/", "");
            if (moduleWsdl.containsKey(moduleName)) {
                moduleWsdl.get(moduleName).add(wsdlPath);
            } else {
                Set<String> wsdlPaths = new HashSet<>();
                wsdlPaths.add(wsdlPath);
                moduleWsdl.put(moduleName, wsdlPaths);
            }
        }
    }

    private void filterEndpointRow (Row row) {
        if (row.getCell(0).getCellType() == CellType.STRING &&
                row.getCell(0).getStringCellValue().contains("wsdl")) {
            String endpoint = row.getCell(0).getStringCellValue();
            endpoints.add(endpoint);
        }
    }

    private void buildContent() throws Exception {
        for (String moduleName : moduleWsdl.keySet()) {
            List<Path> paths = PathHelper.getPathsForModule(moduleName, scanPath);
            for (String wsdlPath : moduleWsdl.get(moduleName)) {
                addTrackClassForModule(moduleName, wsdlPath, paths, endpoints);
            }
        }
    }

    private void addTrackClassForModule(String moduleName, String wsdl, List<Path> paths, Set<String> endpoints) throws Exception {
        for (Path p : paths) {
            if (Files.lines(p).anyMatch(line -> line.contains(wsdl))) {
                if (content.getContent().containsKey(moduleName)) {
                    content.getContent().get(moduleName).put(PathHelper.getPackageClass(p.toString()), addTrackMethodsForClass(endpoints, wsdl));
                } else {
                    Map<String, List<String>> classMethod = new HashMap<>();
                    classMethod.put(PathHelper.getPackageClass(p.toString()), addTrackMethodsForClass(endpoints, wsdl));
                    content.getContent().put(moduleName, classMethod);
                }
            }
        }
    }

    private List<String> addTrackMethodsForClass(Set<String> endpoints, String wsdlPath) throws Exception {
        List<String> methods = new ArrayList<>();
        try {
            for (String endpoint : endpoints) {
                String endpointWsdl = endpoint.substring(endpoint.lastIndexOf("/") + 1).replaceFirst("\\?", ".");
                String wsdl = wsdlPath.substring(wsdlPath.lastIndexOf("/") + 1);
                if (endpointWsdl.equalsIgnoreCase(wsdl)) {
                    methods.addAll(WsdlParser.getAllMethodsName(endpoint));
                }
            }
        } catch (IOException| SAXException e) {
            throw new Exception( "Error while get methods name from wsdl");
        }
        return methods;
    }


    static class PathHelper {

        static String getPackageClass(String classPath) {
            return classPath
                    .substring(classPath.indexOf("ru"))
                    .replace(".java", "")
                    .replace(java.io.File.separator, ".");
        }

        static List<Path> getPathsForModule(String moduleName, Path scanPath) throws IOException {
            return findSrcPaths(moduleName,scanPath);
        }

        private static List<Path> findSrcPaths(String moduleName, Path scanPath) throws IOException {
            return Files.find(scanPath, Integer.MAX_VALUE,
                    (p, a) -> p.toString().endsWith(".java") && p.toString().contains("src") && p.toString()
                            .contains(moduleName))
                    .collect(Collectors.toList());
        }



    }

}
