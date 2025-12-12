package com.microfinance.accounting_microservice.service.exchangerate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

@Service
@Slf4j
public class BcnSoapService {

    @Value("${exchange-rate.bcn.wsdl-url}")
    private String wsdlUrl;

    public Double getOfficialRate() {
        try {
            // Construir el request XML manualmente para evitar complexidad de JAXB con este
            // WSDL específico
            // El servicio RecuperaTasaDia toma Dia, Mes, Anio
            LocalDate now = LocalDate.now();
            String requestPayload = "<RecuperaTC_Dia xmlns=\"http://servicios.bcn.gob.ni/\">\n" +
                    "  <Dia>" + now.getDayOfMonth() + "</Dia>\n" +
                    "  <Mes>" + now.getMonthValue() + "</Mes>\n" +
                    "  <Ano>" + now.getYear() + "</Ano>\n" +
                    "</RecuperaTC_Dia>";

            WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
            webServiceTemplate.setDefaultUri("https://servicios.bcn.gob.ni/Tc_Servicio/ServicioTC.asmx");

            // Configurar SSL Context permisivo para soportar TLSv1 legacy del BCN
            HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender() {
                @Override
                protected void prepareConnection(HttpURLConnection connection) throws java.io.IOException {
                    if (connection instanceof HttpsURLConnection) {
                        try {
                            // Crear un TrustManager que confíe en todo (para evitar problemas de
                            // certificados con legacy server)
                            TrustManager[] trustAllCerts = new TrustManager[] {
                                    new X509TrustManager() {
                                        public X509Certificate[] getAcceptedIssuers() {
                                            return null;
                                        }

                                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                        }

                                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                        }
                                    }
                            };

                            // Inicializar SSLContext habilitando explícitamente TLSv1
                            SSLContext sc = SSLContext.getInstance("TLSv1");
                            sc.init(null, trustAllCerts, new java.security.SecureRandom());

                            ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
                            ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                        } catch (Exception e) {
                            throw new RuntimeException("Error configurando SSL legacy", e);
                        }
                    }
                    super.prepareConnection(connection);
                }
            };
            webServiceTemplate.setMessageSender(messageSender);

            StreamSource source = new StreamSource(new StringReader(requestPayload));
            StringWriter resultWriter = new StringWriter();
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(resultWriter);

            // SOAP Action header es requerido por este servicio .NET
            webServiceTemplate.sendSourceAndReceiveToResult(
                    source,
                    new SoapActionCallback("http://servicios.bcn.gob.ni/RecuperaTC_Dia"),
                    result);

            String responseXml = resultWriter.toString();
            return parseRateFromXml(responseXml);

        } catch (Exception e) {
            log.error("Error consumiendo servicio SOAP BCN: {}", e.getMessage());
            throw new RuntimeException("Error obteniendo tasa oficial", e);
        }
    }

    private Double parseRateFromXml(String xml) {
        try {
            log.info("Analizando XML del BCN: {}", xml);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Important: namespace aware
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            // Buscar por nombre de tag local, ignorando prefijos de namespace
            NodeList nodes = doc.getElementsByTagNameNS("*", "RecuperaTC_DiaResult");
            if (nodes.getLength() == 0) {
                // Fallback a búsqueda simple por si el parser no maneja bien NS
                nodes = doc.getElementsByTagName("RecuperaTC_DiaResult");
            }

            if (nodes.getLength() > 0) {
                String rateStr = nodes.item(0).getTextContent().trim();
                log.info("Tasa extraída del XML: {}", rateStr);
                double val = Double.parseDouble(rateStr);
                if (val <= 0) {
                    log.warn("Tasa retornada es 0.0, posible error en parámetros de fecha o servicio");
                    return null; // O lanzar excepción
                }
                return val;
            }
            log.warn("No se encontró elemento RecuperaTC_DiaResult en la respuesta XML");
            return null;
        } catch (Exception e) {
            log.error("Error parseando respuesta XML BCN: {}", e.getMessage());
            return null;
        }
    }
}
