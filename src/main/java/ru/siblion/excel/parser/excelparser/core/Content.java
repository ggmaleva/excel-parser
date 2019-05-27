package ru.siblion.excel.parser.excelparser.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Content<K,V> {


    Map<K,V> getContent () ;

    void  setContent (Map<K,V> content);

}
