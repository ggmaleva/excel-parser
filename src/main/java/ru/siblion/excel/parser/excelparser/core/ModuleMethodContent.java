package ru.siblion.excel.parser.excelparser.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleMethodContent implements Content <String, Map<String,List<String>>> {

    private Map<String, Map<String,List<String>>> content;

    public ModuleMethodContent() {

        this.content = new HashMap<>();
    }

    @Override
    public Map<String, Map<String,List<String>>> getContent() {
        return content;
    }

    public void setContent( Map<String, Map<String,List<String>>> content) {
        this.content = content;
    }
}
