package hu.nje.beadando.controller;

import hu.nje.beadando.soapclient.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import hu.nje.beadando.model.*;

import java.util.List;
import java.util.ArrayList;

@Controller
public class SoapController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/soap")
    public String showForm(Model model) {
        model.addAttribute("rateRequest", new RateRequest());
        return "soap";
    }

    @PostMapping("/soap")
    public String queryRates(@ModelAttribute RateRequest request, Model model) {
        List<Rate> rates = new ArrayList<>();
        try {
            MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
            MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();

            String xml = service.getExchangeRates(
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getCurrency()
            );

            rates = parseRates(xml, request.getCurrency());

        } catch (Exception e) {
            model.addAttribute("error", "Hiba: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("rates", rates);
        model.addAttribute("currency", request.getCurrency());
        model.addAttribute("rateRequest", request);
        return "soap";
    }

    private List<Rate> parseRates(String xml, String currency) {
        List<Rate> result = new ArrayList<>();
        try {
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes("UTF-8")));

            var dayNodes = doc.getElementsByTagName("Day");

            for (int i = 0; i < dayNodes.getLength(); i++) {
                var day = (org.w3c.dom.Element) dayNodes.item(i);
                String date = day.getAttribute("date");

                var rateNodes = day.getElementsByTagName("Rate");
                for (int j = 0; j < rateNodes.getLength(); j++) {
                    var rate = (org.w3c.dom.Element) rateNodes.item(j);
                    String curr = rate.getAttribute("curr");
                    String valueStr = rate.getTextContent().replace(",", ".");


                    if (currency.equals(curr) && !valueStr.isEmpty()) {
                        double value = Double.parseDouble(valueStr);
                        result.add(new Rate(date, value));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getText(org.w3c.dom.Element parent, String tag) {
        var node = parent.getElementsByTagName(tag).item(0);
        return node != null ? node.getTextContent() : "";
    }

}

//class RateRequest {
//    private String currency;
//    private String startDate;
//    private String endDate;
//
//    public String getCurrency() {
//        return currency;
//    }
//    public void setCurrency(String currency) { this.currency = currency; }
//    public String getStartDate() {return startDate;}
//    public void setStartDate(String startDate) { this.startDate = startDate;}
//    public String getEndDate() {return endDate;}
//    public void setEndDate(String endDate) { this.endDate = endDate;}
//}

//class Rate {
//    private String date;
//    private double value;
//    private String currency = "HUF";
//
//    public Rate(String date, double value) {
//        this.date = date;
//        this.value = value;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public double getValue() {
//        return value;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//}

