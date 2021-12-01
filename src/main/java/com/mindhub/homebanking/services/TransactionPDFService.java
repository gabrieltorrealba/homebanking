package com.mindhub.homebanking.services;

import com.lowagie.text.pdf.PdfPTable;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface TransactionPDFService {
    public void export(HttpServletResponse response, Client client1, Client client2, String accountOrigin, String accountDestination,
                       String amount, String description) throws IOException;

    public void exportTransaction(HttpServletResponse response, Client client, Account account, Set<Transaction> transactions) throws IOException;
}
