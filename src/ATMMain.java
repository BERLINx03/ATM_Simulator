import atm.ATMGUI;
import services.AccountService;
import services.SecurityUtils;

import javax.swing.*;

public static void main(String[] args) {

    AccountService accountService = new AccountService();
    SwingUtilities.invokeLater(new ATMGUI(accountService));

}
