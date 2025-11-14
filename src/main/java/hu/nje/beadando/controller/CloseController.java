// src/main/java/hu/nje/beadando/controller/ForexCloseController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.trade.TradeID;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeSpecifier;
import hu.nje.beadando.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CloseController {

    @GetMapping("/close")
    public String showCloseForm(Model model) {
        return "close";
    }

    @PostMapping("/close")
    public String closePosition(@RequestParam String tradeIdStr, Model model) {
        if (tradeIdStr == null || tradeIdStr.isBlank()) {
            model.addAttribute("error", "Trade ID kötelező!");
            return "close";
        }

        try {
            long tradeId = Long.parseLong(tradeIdStr);
            Context ctx = new Context(Config.URL, Config.TOKEN);
            TradeID tid = new TradeID(String.valueOf(tradeId));
            TradeSpecifier spec = new TradeSpecifier(tid);

            // POZÍCIÓ LEZÁRÁSA (piaci áron, teljes mennyiség)
            TradeCloseRequest request = new TradeCloseRequest(new AccountID(Config.ACCOUNTID), spec);
            request.setUnits("ALL");

            ctx.trade.close(request);

            model.addAttribute("success", "Pozíció lezárva! Trade ID: " + tradeId);

        } catch (NumberFormatException e) {
            model.addAttribute("error", "Érvénytelen Trade ID! Szám legyen.");
        } catch (Exception e) {
            model.addAttribute("error", "Lezárási hiba: " + e.getMessage());
            e.printStackTrace();
        }

        return "close";
    }
}