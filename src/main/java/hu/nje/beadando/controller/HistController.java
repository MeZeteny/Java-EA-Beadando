// src/main/java/hu/nje/beadando/controller/ForexHistController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.InstrumentName;
import hu.nje.beadando.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class HistController {

    private static final List<String> INSTRUMENTS = Arrays.asList(
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF", "AUD_USD"
    );

    private static final List<String> GRANULARITIES = Arrays.asList(
            "S5", "M1", "M5", "M15", "H1", "H4", "D", "W"
    );

    @GetMapping("/history")
    public String showHist(
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String granularity,
            Model model) {

        model.addAttribute("instruments", INSTRUMENTS);
        model.addAttribute("granularities", GRANULARITIES);
        model.addAttribute("selectedInst", instrument);
        model.addAttribute("selectedGran", granularity);

        if (instrument != null && granularity != null && !instrument.isBlank() && !granularity.isBlank()) {
            try {
                Context ctx = new Context(Config.URL, Config.TOKEN);
                InstrumentName instr = new InstrumentName(instrument);
                CandlestickGranularity gran = CandlestickGranularity.valueOf(granularity);

                InstrumentCandlesRequest request = new InstrumentCandlesRequest(instr);
                request.setGranularity(gran);
                request.setCount(10L); // utolsó 10 gyertya

                InstrumentCandlesResponse response = ctx.instrument.candles(request);
                List<Candlestick> candles = response.getCandles();

                model.addAttribute("candles", candles);

            } catch (Exception e) {
                model.addAttribute("error", "Történeti adat hiba: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return "history";
    }
}