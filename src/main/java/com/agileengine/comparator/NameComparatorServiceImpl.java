package com.agileengine.comparator;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

public class NameComparatorServiceImpl implements NameComparatorService {


    private final double Percent = 0.05;



    private static Map<Element, Integer> getScoreForElementAttrEquals(Element element, Document doc) {
        Map<Element, Integer> scoreMap = new HashMap<>();
        Elements elements = doc.getAllElements();

        for (Element e : elements) {
            Integer score = 0;
            for (Attribute attribute : element.attributes()) {
                score = score + getTotalScore(e.attributes().get(attribute.getKey()), attribute.getValue());
            }
            scoreMap.put(e, score);
        }
        return scoreMap;
    }

    public static Element getBestVariant(Element element, Document document) {
        Map<Element,Integer> map = getScoreForElementAttrEquals(element,document);
        Integer currentScore = 0;
        Element currElement = null;
        for (Element e : map.keySet()) {
            if (map.get(e) > currentScore) {
                currentScore = map.get(e);
                currElement = e;
            }
        }
        return currElement;
    }

    @Override
    public Map<String, Integer> getScoreForElementIdEquals(String elementName, Document doc) {
        Map<String, Integer> scoreMap = new HashMap<>();
        Elements elements = doc.getAllElements();
        List<String> sources = new ArrayList<>();
        for (Element element : elements) {
            sources.add(element.id());
        }
        Integer score = 0;
        for (String name : sources) {
            score = getTotalScore(elementName, name);
            scoreMap.put(name, score);
        }

        scoreMap = filteredScoreValuesByPercentOfMaxScore(scoreMap, Percent);

        return scoreMap;
    }


    private static Integer getTotalScore(String search, String source) {
        Integer resultScore = 0;
        resultScore = getPreviousScore(search, source) + getBonusScoreForNumber(search, source) + getScoreWithMonolitSearch(search, source);
        return resultScore;
    }

    private static Integer getPreviousScore(String search, String source) {
        Integer resultScore = 0;
        char[] searchSymbols = search.toCharArray();
        char[] sourceSymbols = source.toCharArray();
        for (int i = 0; i < searchSymbols.length; i++) {
            for (int j = 0; j < sourceSymbols.length; j++) {
                if (searchSymbols[i] == sourceSymbols[j] && i == j) {
                    resultScore = resultScore + 50;
                } else if (Character.toUpperCase(searchSymbols[i]) == Character.toUpperCase(sourceSymbols[j]) && i == j) {
                    resultScore = resultScore + 40;
                } else if (searchSymbols[i] == sourceSymbols[j]) {
                    resultScore = resultScore + 30;
                } else if (Character.toUpperCase(searchSymbols[i]) == Character.toUpperCase(sourceSymbols[j])) {
                    resultScore = resultScore + 20;
                } else if (searchSymbols[i] != sourceSymbols[j] && i == j) {
                    resultScore = resultScore - 10;
                } else if (Character.toUpperCase(searchSymbols[i]) == Character.toUpperCase(sourceSymbols[j]) && i == j) {
                    resultScore = resultScore - 5;
                }
            }
        }
        return resultScore;
    }

    private static Integer getBonusScoreForNumber(String search, String source) {
        Integer resultScore = 0;
        char[] searchArray = search.toCharArray();
        char[] sourceArray = source.toCharArray();
        for (int i = 0; i < searchArray.length; i++) {
            for (int j = 0; j < sourceArray.length; j++) {
                if (Character.isDigit(searchArray[i]) && searchArray[i] == sourceArray[j] && i == j) {
                    resultScore = resultScore + 50;
                } else if (Character.isDigit(searchArray[i]) && searchArray[i] == sourceArray[j]) {
                    resultScore = resultScore + 30;
                } else if (Character.isDigit(searchArray[i]) && searchArray[i] != sourceArray[j] && i == j) {
                    resultScore = resultScore - 25;
                } else if (Character.isDigit(searchArray[i]) && Character.isDigit(sourceArray[j]) && searchArray[i] != sourceArray[j]) {
                    resultScore = resultScore - 15;
                }
            }
        }
        return resultScore;
    }

    private static Integer getScoreWithMonolitSearch(String search, String source) {
        char[] searchArray = search.toCharArray();
        char[] sourceArray = source.toCharArray();
        Integer resultScore = 0;

        List<Character> searchArrayLettersAndDigits = new LinkedList<>();
        List<Character> sourceArrayLettersAndDigits = new LinkedList<>();

        for (int i = 0; i < searchArray.length; i++) {
            if (Character.isLetterOrDigit(searchArray[i])) {
                searchArrayLettersAndDigits.add(searchArray[i]);
            }
        }
        for (int i = 0; i < sourceArray.length; i++) {
            sourceArrayLettersAndDigits.add(sourceArray[i]);
        }

        for (int i = 0; i < searchArrayLettersAndDigits.size(); i++) {
            for (int j = 0; j < sourceArrayLettersAndDigits.size(); j++) {
                if (searchArrayLettersAndDigits.get(i) == sourceArrayLettersAndDigits.get(j) && i == j) {
                    resultScore = resultScore + 80;
                } else if (Character.toUpperCase(searchArrayLettersAndDigits.get(i)) == Character.toUpperCase(sourceArrayLettersAndDigits.get(j)) && i == j) {
                    resultScore = resultScore + 60;
                }

            }
        }
        return resultScore;

    }

    private Map<Integer, String> filterValues(Map<Integer, String> names, double epsilum) {
        int[] array = new int[names.keySet().size()];
        int counter = 0;

        for (int i : names.keySet()) {
            array[counter] = i;
            counter++;
        }
        Map<Integer, String> filteredMap = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j <= array.length - 1; j++) {
                double result = Math.abs(array[i] / array[j]);
                if (result > (1 - epsilum) && result < (1 + epsilum)) {
                    filteredMap.put(array[i], names.get(array[i]));
                    filteredMap.put(array[j], names.get(array[j]));
                }
            }
        }
        return filteredMap;

    }

    private Set<Integer> filterValues(Set<Integer> keySet, double epsilum) {
        int[] array = new int[keySet.size()];
        int counter = 0;
        for (int i : keySet) {
            array[counter] = i;
            counter++;
        }
        Set<Integer> filteredSet = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j <= array.length - 1; j++) {
                double result = Math.abs(array[i] / array[j]);
                if (result > (1 - epsilum) && result < (1 + epsilum)) {
                    filteredSet.add(i);
                    filteredSet.add(j);
                }
            }
        }
        return filteredSet;

    }


    private Map<String, Integer> filteredScoreValuesByPercentOfMaxScore(Map<String, Integer> notFilteredMap, double percent) {
        int maxValue = 0;

        for (String s : notFilteredMap.keySet()) {
            int currentScore = notFilteredMap.get(s);
            if (currentScore > maxValue) {
                maxValue = currentScore;
            }
        }

        int limitValue = (int) (maxValue * percent);
        Map<String, Integer> filteredMap = new HashMap<>();

        for (String s : notFilteredMap.keySet()) {
            int currentValue = notFilteredMap.get(s);
            if (currentValue > limitValue) {
                filteredMap.put(s, currentValue);
            }
        }

        return filteredMap;
    }

}
