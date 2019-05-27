package ru.siblion.excel.parser.excelparser.core;

import java.nio.file.Path;

public class ExcelToXMLParser implements Parser {

    private Path source;
    private Path scanPath;

    public ExcelToXMLParser(Path source, Path scanPath) {
        this.source = source;
        this.scanPath = scanPath;
    }

    public Report parse () throws Exception{
        Importer importerExcel = new ImporterExcel(source, scanPath);
        importerExcel.putContent();
        ExporterXML exporter = new ExporterXML();
        return new XMLReport(importerExcel.getContent(), exporter);
    }

}
