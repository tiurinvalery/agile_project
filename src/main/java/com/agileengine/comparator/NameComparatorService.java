package com.agileengine.comparator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

public interface NameComparatorService {
    Map<String, Integer> getScoreForElementIdEquals(String elementName, Document doc);

}
