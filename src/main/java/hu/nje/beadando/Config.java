// src/main/java/hu/nje/beadando/Config.java
package hu.nje.beadando;

import com.oanda.v20.account.AccountID;

public class Config {
    private Config() {} // ne lehessen példányosítani

    // DEMO környezet
    public static final String URL = "https://api-fxpractice.oanda.com";

    // A TE DEMO FIÓKOD ADATAI
    public static final AccountID ACCOUNTID = new AccountID("101-004-37679294-001");
    public static final String TOKEN = "5fb620dc03edf6188966c0b96512b0d3-27e8b435c114bc525504f65b73fdced6";
}