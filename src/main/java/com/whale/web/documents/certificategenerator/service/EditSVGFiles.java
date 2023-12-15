package com.whale.web.documents.certificategenerator.service;

import com.whale.web.documents.certificategenerator.model.Certificate;
import com.whale.web.documents.certificategenerator.model.enums.CertificateBasicInfosEnum;
import com.whale.web.documents.certificategenerator.model.enums.PersonBasicInfosEnum;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.domain.WhaleTransformerException;
import com.whale.web.utils.FormatDataUtil;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EditSVGFiles {

    public List<String> cretateListCertificate(Certificate certificate, List<String> names, String certificateTemplate) {
        Document patchModel = preparateCertificateWithBasicInfos(certificate, certificateTemplate);
        List<String> listCertificates = new ArrayList<>();

        for (String name : names) {

            String certificateForPerson = preparateCertificateForPerson(name, patchModel);

            listCertificates.add(certificateForPerson);
        }

        return listCertificates;
    }

    private String preparateCertificateForPerson(String personName, Document document){
        List<PersonBasicInfosEnum> basicInfos = Arrays.asList(PersonBasicInfosEnum.values());

        NodeList textElements = document.getElementsByTagName("tspan");

        for (int i = 0; i < textElements.getLength(); i++) {
            Element textElement = (Element) textElements.item(i);
            String id = textElement.getAttribute("id");
            for (PersonBasicInfosEnum basicInfo : basicInfos) {
                if (id.equals(basicInfo.getTagId())) {
                    switch (basicInfo) {
                        case PERSON_NAME, PERSON_NAME_BODY -> textElement.setTextContent(personName);
                    }
                }
            }
        }
        return convertDocumentToString(document);
    }

    private Document preparateCertificateWithBasicInfos(Certificate certificate, String certificateTemplate) {
        List<CertificateBasicInfosEnum> basicInfos = Arrays.asList(CertificateBasicInfosEnum.values());

        Document document = readSVG(certificateTemplate);
        NodeList textElements = document.getElementsByTagName("tspan");

        for (int i = 0; i < textElements.getLength(); i++) {
            Element textElement = (Element) textElements.item(i);
            String id = textElement.getAttribute("id");
            for (CertificateBasicInfosEnum basicInfo : basicInfos) {
                if (id.equals(basicInfo.getTagId())) {
                    switch (basicInfo) {
                        case EVENT_NAME, EVENT_NAME_BODY -> textElement.setTextContent(certificate.getEventName());
                        case SPEAKER_NAME -> textElement.setTextContent(certificate.getSpeakerName());
                        case SPEAKER_ROLE -> textElement.setTextContent(certificate.getSpeakerRole());
                        case EVENT_DATE, EVENT_DATE_BODY -> textElement.setTextContent(FormatDataUtil.formatData(
                                certificate.getEventDate()));
                        case WORKLOAD -> textElement.setTextContent(certificate.getEventWorkLoad());
                        case EVENT_LOCALE -> textElement.setTextContent(certificate.getEventLocale());
                    }
                }
            }
        }
        return document;
    }

    private Document readSVG(String certificateTemplate) {
        try {
            File svgFile = new File(certificateTemplate);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            return builder.parse(svgFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new WhaleRunTimeException(e.getMessage());
        }
    }

    private String convertDocumentToString(Document document) {
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        }catch (TransformerException e){
            throw new  WhaleTransformerException(e.getMessage());
        }
    }
}
