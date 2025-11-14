// src/main/java/hu/nje/beadando/controller/ForexController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.InstrumentName;
import hu.nje.beadando.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class AktArController {

    private static final List<String> INSTRUMENTS = Arrays.asList(
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF", "AUD_USD", "NZD_USD"
    );

    @GetMapping("/aktar")
    public String showAktar(@RequestParam(required = false) String instrument, Model model) {
        model.addAttribute("instruments", INSTRUMENTS);
        model.addAttribute("selected", instrument);

        if (instrument != null && !instrument.isBlank()) {
            try {
                Context ctx = new Context(Config.URL, Config.TOKEN);
                InstrumentName instr = new InstrumentName(instrument);
                List<InstrumentName> instruments = Collections.singletonList(instr);

                // ÁR LEKÉRDEZÉSE
                PricingGetResponse response = ctx.pricing.get(Config.ACCOUNTID, instruments);
                ClientPrice price = response.getPrices().get(0); // első ár

                model.addAttribute("bid", price.getBids().get(0).getPrice().toString());
                model.addAttribute("ask", price.getAsks().get(0).getPrice().toString());
                model.addAttribute("time", price.getTime().toString());

            } catch (Exception e) {
                model.addAttribute("error", "Ár lekérdezési hiba: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return "aktar";
    }
}