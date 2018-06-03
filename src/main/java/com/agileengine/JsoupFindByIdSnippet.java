package com.agileengine;

import com.agileengine.comparator.NameComparatorServiceImpl;
import com.agileengine.model.AttributeEntity;
import com.agileengine.model.Button;
import com.agileengine.repository.AttributeRepository;
import com.agileengine.repository.ButtonRepository;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JsoupFindByIdSnippet {

    private static Logger LOGGER = LoggerFactory.getLogger(JsoupFindByIdSnippet.class);

    private static String CHARSET_NAME = "utf8";

    public static void main(String[] args) {

        // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
        // so providing InputStream through classpath resources is not a case
        String resourcePath = "./samples/startbootstrap-freelancer-gh-pages-cut.html";
        String targetElementId = "make-all-ok-button"; //original ID
        Optional<Element> buttonOpt = findElementById(new File(resourcePath), targetElementId);



        Optional<List<Attribute>> attributes = buttonOpt.map(button -> button.attributes().asList());
        List<Attribute> attributeList = attributes.orElse(null);

        Session session = HibernateUtill.getSessionFactory().openSession();

        Button rightBtn = new Button();
        try {
            rightBtn.setNumberOfAttributes(attributeList.size());
        } catch (NullPointerException ex) {
            rightBtn.setNumberOfAttributes(0);
        }
        rightBtn.setRightId(targetElementId);
        ButtonRepository.createButton(rightBtn, session);

        if (attributeList != null) {
            for (Attribute attribute : attributeList) {
                AttributeEntity entity = new AttributeEntity();
                entity.setButton(rightBtn);
                entity.setName(attribute.getKey());
                entity.setValue(attribute.getValue());
                AttributeRepository.createAttribute(entity, session);
            }
        }

        Optional<String> stringifiedAttributesOpt = buttonOpt.map(button ->
                button.attributes().asList().stream()
                        .map(attr -> attr.getKey() + " = " + attr.getValue())
                        .collect(Collectors.joining(", "))
        );


        Element potentialBtn = NameComparatorServiceImpl.getBestVariant(buttonOpt.orElse(null), getDocument(new File(resourcePath))); //New comparator algorithm.
        System.out.println("NEW: "+ potentialBtn.id()+"");

        stringifiedAttributesOpt.ifPresent(attrs -> LOGGER.info("Target element attrs: [{}]", attrs));
        session.close();
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Document getDocument(File htmlFile) {

        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath()

            );
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }


}
