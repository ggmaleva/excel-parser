package ru.siblion.excel.parser.excelparser.core;

public class XMLReport implements Report {

    private Content content;
    private Exporter exporter;

    public XMLReport(Content content, Exporter exporter) {
        this.content = content;
        this.exporter = exporter;
    }

    public void execute () throws Exception{

        exporter.export(content);
    }


}
