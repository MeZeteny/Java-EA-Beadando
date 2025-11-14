// src/main/java/hu/nje/beadando/controller/ForexPositionsController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.trade.Trade;
import hu.nje.beadando.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PositionController {

    @GetMapping("/positions")
    public String showPositions(Model model) {
        try {
            Context ctx = new Context(Config.URL, Config.TOKEN);

            // Nyitott pozíciók lekérdezése
            List<Trade> trades = ctx.trade.listOpen(Config.ACCOUNTID).getTrades();

            model.addAttribute("trades", trades);
            model.addAttribute("hasTrades", !trades.isEmpty());

        } catch (Exception e) {
            model.addAttribute("error", "Pozíciók lekérdezése sikertelen: " + e.getMessage());
            e.printStackTrace();
        }

        return "positions"; // → forex_positions.html
    }
}