package textannotation;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import main.MAG2RDF;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringEscapeUtils;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ValidityException;


import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.ParsingException;

// (c) 2019, Michael Faerber.
// This work is licensed under a Creative Commons Attribution Share-Alike 4.0 License.
public class FullTextAnnotationClientXML {

    public static boolean WITH_PROXY = false;
    public static boolean LOG = false;

    public static String getConcatenatedSentences(List<String> sentences) {
        String doc_str = "";
        for (String sent : sentences) {
            doc_str = doc_str + " " + sent; 
        }
        return doc_str.trim();
    }

    /**
     * same as below getAnnotationsByWholeDocAnnotation. ONLY used in
     * TREC-KBA-Stream/test/AnnotateAndCheck! *
     */
    public List<TextAnnotation> getAnnotationsByWholeDocAnnotation(String doc) throws IOException {
        TextAnnotationWSClientv2 annotser = new TextAnnotationWSClientv2();
        String xmlstring = "";
        xmlstring = annotser.retrieveXMLFromServiceGivenWholeDoc(StringEscapeUtils.escapeXml(doc));
        xmlstring = xmlstring.replaceAll("\\n", ""); // TODO: simple XML processing ;) (remove linebreaks)
        List<TextAnnotation> listOfAnnotationsInDoc = getAllAnnotations(xmlstring);
        return listOfAnnotationsInDoc;
    }

    /**
     * Annotate whole input text document at once.
     *
     * @param currDoc
     * @throws IOException
     */
    // formerly as first input paramter: Document
    public List<TextAnnotation> getAnnotationsByWholeDocAnnotation(List<String> sentencesOfCurrDoc, boolean ANNOTATE_ONLY_ENTITYNODES) throws IOException {
        TextAnnotationWSClientv2 annotser = new TextAnnotationWSClientv2();

        String xmlstring = "";
        /// ESCAPED here for text annotation, 2014-12-03 (also needed at SRL input, check!)
        xmlstring = annotser.retrieveXMLFromServiceGivenWholeDoc(StringEscapeUtils.escapeXml(getConcatenatedSentences(sentencesOfCurrDoc)));
        xmlstring = xmlstring.replaceAll("\\n", ""); // TODO: simple XML processing ;) (remove linebreaks)
//		System.out.println(xmlstring);

        annotser.close();

        List<TextAnnotation> listOfAnnotationsInDoc = getAllAnnotations(xmlstring);
        if (ANNOTATE_ONLY_ENTITYNODES == true) {
            // Filter here..// annotate only named entities (type EntityNode) and not non-named entities (type WordNode)
        }
        return listOfAnnotationsInDoc;

    }

    // transform xml string into list of (filled) Annotation objects
    public static List<TextAnnotation> getAllAnnotations(String xmlstring) {
//		System.out.println(xmlstring);
        List<TextAnnotation> detectedAnnots = new ArrayList<>();
        Builder builder = new Builder();
        Document doc = null;
        Nodes annotationNodes = new Nodes();
        try {
            doc = builder.build(new ByteArrayInputStream(xmlstring.getBytes()));
        } catch (ValidityException e) {
            e.printStackTrace();
            return null;
        } catch (ParsingException ex) {
            Logger.getLogger(FullTextAnnotationClientXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FullTextAnnotationClientXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (doc != null) {
            annotationNodes = doc.query("/item/annotations/annotation");
        }

        for (int i = 0; i < annotationNodes.size(); i++) {
            Element ele = (Element) annotationNodes.get(i);
            TextAnnotation an = new TextAnnotation();
            // 1. attributes of anotation
            an.setDisplayName(ele.getAttributeValue("displayName"));
            an.setEntityID(Integer.valueOf(ele.getAttributeValue("entityId")));
            an.setWeight(Float.valueOf(ele.getAttributeValue("weight")));

            // 2. under description
            Element descriptions = ele.getFirstChildElement("descriptions"); // there should be exactly one "descriptions"
            for (int j = 0; j < descriptions.getChildCount(); j++) { // assumed: all children are "description" nodes
                Element ele2 = (Element) descriptions.getChild(j);
                if (ele2.getAttributeValue("lang").equals("en")) {
                    an.setURL_EN(ele2.getAttributeValue("URL"));
                } else if (ele2.getAttributeValue("lang").equals("es")) {
                    an.setURL_ES(ele2.getAttributeValue("URL"));
                } else if (ele2.getAttributeValue("lang").equals("fr")) {
                    an.setURL_FR(ele2.getAttributeValue("URL"));
                } else if (ele2.getAttributeValue("lang").equals("de")) {
                    an.setURL_DE(ele2.getAttributeValue("URL"));
                } else if (ele2.getAttributeValue("lang").equals("zh")) {
                    an.setURL_ZH(ele2.getAttributeValue("URL"));
                }
            }

            // 3. under mentions
            Element elemMentions = ele.getChildElements("mentions").get(0);// there should be exactly one "mentions" in xml output
            // create list of mentions
            List<MentionInAnnotation> listofments = new ArrayList<MentionInAnnotation>();
            for (int j = 0; j < elemMentions.getChildCount(); j++) {
                Element elemOfMention = (Element) elemMentions.getChild(j);
                if (LOG == true) {
                    System.out.println("added:: " + elemOfMention.getAttributeValue("words") + "_" + Integer.valueOf(elemOfMention.getAttributeValue("start")) + "_" + Integer.valueOf(elemOfMention.getAttributeValue("end")));
                }
                MentionInAnnotation mia = new MentionInAnnotation(elemOfMention.getAttributeValue("words"), Integer.valueOf(elemOfMention.getAttributeValue("start")), Integer.valueOf(elemOfMention.getAttributeValue("end")));
                listofments.add(mia);
            }
            an.setListOfMentions(listofments);

            detectedAnnots.add(an);
        }
        return detectedAnnots; // return list of annotation objects
    }

    // added November 2015:
    // often line breaks, use " " to concatenate parts.
    public static String getArticleBodyWithWS(String xmlstring) {

        String text = "";
        Builder builder = new Builder();
        Document doc = null;
        try {
            doc = builder.build(new ByteArrayInputStream(xmlstring.getBytes("UTF-8")));
        } catch (ValidityException e) {
            e.printStackTrace();
            return null;
        } catch (ParsingException ex) {
            Logger.getLogger(FullTextAnnotationClientXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FullTextAnnotationClientXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        Nodes textNodes = doc.query("/item/text");

        for (int i = 0; i < textNodes.size(); i++) { // actually only 1 text field per xml..

            Node textNode = textNodes.get(i);

            Element ele = (Element) textNode;

            text = text + " " + ele.getValue();

        }

        return text;

    }

    // fitting for service (xml) http://X.X.X.X:8080/text-annotation-with-offset-Nov14/
    public static class TextAnnotationWSClientv2 {

        private WebResource webResource;
        private Client client;
        // so far monolingual
        private static final String TEXT_ANNOT_BASE_URI = MAG2RDF.xlisaTextannotationsServiceURI; //"http://X.X.X.X:8080/text-annotation-with-offset-Nov14";

        public TextAnnotationWSClientv2() {
            ClientConfig config = new DefaultClientConfig();

            if (WITH_PROXY == true) {
                client = createClientWithProxyConnection(config);
            } else {
                client = Client.create(config);
            }

            webResource = client.resource(TEXT_ANNOT_BASE_URI);
        }

        private Client createClientWithProxyConnection(ClientConfig config) {
            // with proxy
            return client = new Client(new URLConnectionClientHandler(
                    new HttpURLConnectionFactory() {
                        Proxy p = null;

                        @Override
                        public HttpURLConnection getHttpURLConnection(URL url)
                        throws IOException {
                            if (p == null) {
                                if (System.getProperties().containsKey("http.proxyHost")) {
                                    p = new Proxy(Proxy.Type.HTTP,
                                            new InetSocketAddress(
                                                    System.getProperty("http.proxyHost"),
                                                    Integer.getInteger("http.proxyPort", 80)));
                                } else {
                                    p = Proxy.NO_PROXY;
                                }
                            }
                            return (HttpURLConnection) url.openConnection(p);
                        }
                    }), config);
        }

        /**
         * input is one string, should be the whole input text document which is
         * annotated (2nd annotation version).
         *
         * @param docAsString
         * @return
         * @throws UniformInterfaceException
         */
        public String retrieveXMLFromServiceGivenWholeDoc(String docAsString)
                throws UniformInterfaceException {

            for (int retries = 0;; retries++) {
                try {

                    String strToTransfer = "<item><text>" + docAsString + "</text></item>";
                    ClientResponse response = webResource.path("/").accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_XML).post(ClientResponse.class, strToTransfer);
		        // accept("application/xml").type("application/xml")

                    // check response status code
                    if (response.getStatus() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatus());
                    }
                    if (response.getLength() == 0) {
                        System.err.print("Return Length: " + response.getLength());
                        System.err.print(" Input text maybe too long or special chars, so no end tags.\n");
                        //System.out.print("Text to be annotated is/was:" + docAsString);
                        System.out.println();
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<item>\n<text>\r\n</text>\n<WikifiedText></WikifiedText>\n<annotations>\n</annotations>\n</item>";
                    }

                    // display response
                    String outputXml = response.getEntity(String.class);
                    return outputXml;

                } catch (Exception e) {
                    if (retries < 5) { // try up to 10 times
                        try {
                            Thread.sleep(2000);         // wait
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    } else {
                        System.out.println("not annotated.");
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<item>\n<text>\r\n</text>\n<WikifiedText></WikifiedText>\n<annotations>\n</annotations>\n</item>";
                    }
                }
            }
        }

        public void close() {
            client.destroy();
        }
    }

}
