// src/main/java/hu/nje/beadando/controller/AccountController.java
package hu.nje.beadando.controller;

import com.oanda.v20.Context;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.account.AccountProperties;
import hu.nje.beadando.Config;
import hu.nje.beadando.model.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    private static final String PRACTICE_URL = "https://api-fxpractice.oanda.com";

    // 1. BEJELENTKEZÉS OLDAL (ŰRLAP)
    @GetMapping("/account")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("email", null); // null-biztos
        return "account"; // → account.html
    }

    // 2. BEJELENTKEZÉS KEZELÉSE
    @PostMapping("/account")
    public String processLogin(@ModelAttribute LoginRequest request, Model model) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Email és jelszó kötelező");
            model.addAttribute("loginRequest", request);
            return "account";
        }

        try {
            // 1. Context létrehozása jelszóval (Oanda bejelentkezés)
            Context ctx = new Context(PRACTICE_URL, password);

            // 2. Fióklista lekérdezése
            var accountList = ctx.account.list().getAccounts();

            if (accountList.isEmpty()) {
                model.addAttribute("error", "Nincs fiók ehhez a jelszóhoz!");
                model.addAttribute("loginRequest", request);
                return "account";
            }

            // 3. Első fiók kiválasztása
            AccountProperties firstAccount = accountList.get(0);
            AccountID accountId = firstAccount.getId();

            // 4. Fiók adatok lekérdezése (ellenőrzés)
            AccountSummary summary = ctx.account.summary(accountId).getAccount();

            // 5. SIKERES BEJELENTKEZÉS
            model.addAttribute("email", email);
            model.addAttribute("accountId", accountId.toString());
            model.addAttribute("token", password); // a jelszó = token
            model.addAttribute("balance", summary.getBalance() + " " + summary.getCurrency());
            model.addAttribute("openTrades", summary.getOpenTradeCount());
            model.addAttribute("summary", summary); // account_info.html-hoz

        } catch (Exception e) {
            model.addAttribute("error", "Helytelen jelszó vagy fiók hiba!");
            model.addAttribute("loginRequest", request);
            e.printStackTrace();
            return "account";
        }

        return "account"; // adatok megjelenítése
    }

    // 3. VALÓDI ADATOK HTML-ben
    @GetMapping("/account_info")
    public String getAccountInfo(Model model) {
        // Újra bejelentkeztetés (vagy session-ből)
        // Most egyszerűen átirányít
        return "redirect:/account";
    }

    // 4. KIJELENTKEZÉS
    @GetMapping("/account_logout")
    public String logout() {
        return "redirect:/account";
    }
}