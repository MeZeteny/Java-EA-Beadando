// src/main/java/hu/nje/beadando/controller/OpenController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.TradeID;
import hu.nje.beadando.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class OpenController {

    private static final List<String> INSTRUMENTS = Arrays.asList(
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF", "AUD_USD"
    );

    @GetMapping("/open")
    public String showOpenForm(Model model) {
        model.addAttribute("instruments", INSTRUMENTS);
        return "open";
    }

    @PostMapping("/open")
    public String openPosition(
            @RequestParam String instrument,
            @RequestParam Long units,
            Model model) {

        model.addAttribute("instruments", INSTRUMENTS);
        model.addAttribute("selectedInst", instrument);
        model.addAttribute("units", units);

        if (instrument == null || instrument.isBlank() || units == null || units == 0) {
            model.addAttribute("error", "Instrumentum és mennyiség kötelező (0 nem engedélyezett)!");
            return "open";
        }

        try {
            Context ctx = new Context(Config.URL, Config.TOKEN);
            InstrumentName instr = new InstrumentName(instrument);

            // Piaci árú order
            MarketOrderRequest marketOrder = new MarketOrderRequest();
            marketOrder.setInstrument(instr);
            marketOrder.setUnits(units); // + = Long, - = Short

            OrderCreateRequest request = new OrderCreateRequest(Config.ACCOUNTID);
            request.setOrder(marketOrder);

            OrderCreateResponse response = ctx.order.create(request);

            TradeID tradeId = response.getOrderFillTransaction().getTradeOpened().getTradeID();
            model.addAttribute("success", "Pozíció nyitva! Trade ID: " + tradeId);

        } catch (Exception e) {
            model.addAttribute("error", "Pozíció nyitási hiba: " + e.getMessage());
            e.printStackTrace();
        }

        return "open";
    }
}