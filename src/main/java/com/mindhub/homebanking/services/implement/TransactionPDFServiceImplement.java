package com.mindhub.homebanking.services.implement;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.TransactionPDFService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class TransactionPDFServiceImplement implements TransactionPDFService {
    @Override
    public void export(HttpServletResponse response, Client client1, Client client2, String accountOrigin,
                       String accountDestination, String amount, String description) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        double amountDouble = Double.parseDouble(amount);
        Locale locale = new Locale("en", "US");
        NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
        String amountFormatCurrency = currency.format(amountDouble);
        DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String currentDatTime= dateFormat.format(new Date());

        document.open();
        Image image = Image.getInstance("src/main/resources/static/bank_78392.png");
        image.scaleAbsolute(50,50);
        image.setAbsolutePosition(120,760);
        document.add(image);
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(20);
        Color colorBank = new Color(135, 2, 35, 204);
        fontTitle.setColor(colorBank);
        Paragraph paragraph = new Paragraph("Minhub Brothers Banks", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(15);
        Paragraph paragraph1 = new Paragraph("Comprobante de transferencia.", fontParagraph);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph1.setSpacingBefore(15);
        paragraph1.setSpacingAfter(20);
        document.add(paragraph1);

        Font fontDate = FontFactory.getFont(FontFactory.HELVETICA);
        fontDate.setSize(12);
        Paragraph paragraphDate = new Paragraph("Fecha: "+currentDatTime, fontDate);
        paragraphDate.setSpacingAfter(15);
        document.add(paragraphDate);

        Font fontParagraphTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraphTitle.setSize(12);
        Font fontParagraph2 = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph2.setSize(12);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{7.5f, 5.5f});
        PdfPCell cell = new PdfPCell();
        Phrase phrase1 = new Phrase();
        Chunk text1= new Chunk("Cuenta Origen: ", fontParagraphTitle);
        Chunk text2= new Chunk(accountOrigin,fontParagraph2);
        phrase1.add(text1);
        phrase1.add(text2);
        cell.setPhrase(phrase1);
        cell.setBorder(0);
        cell.setExtraParagraphSpace(10);
        table.addCell(cell);

        Phrase phrase2 = new Phrase();
        Chunk text3 = new Chunk("Titular: ", fontParagraphTitle);
        Chunk text4 = new Chunk(client1.getFirstName()+" "+client1.getLastName(), fontParagraph2);
        phrase2.add(text3);
        phrase2.add(text4);
        cell.setPhrase(phrase2);
        table.addCell(cell);

        Phrase phrase3 = new Phrase();
        Chunk text5 = new Chunk("Monto: ", fontParagraphTitle);
        Chunk text6 = new Chunk(amountFormatCurrency,fontParagraph2);
        phrase3.add(text5);
        phrase3.add(text6);
        cell.setPhrase(phrase3);
        table.addCell(cell);

        Phrase phrase4 = new Phrase();
        Chunk text7 = new Chunk("Descripción: ", fontParagraphTitle);
        Chunk text8 = new Chunk(description,fontParagraph2);
        phrase4.add(text7);
        phrase4.add(text8);
        cell.setPhrase(phrase4);
        table.addCell(cell);

        Phrase phrase5 = new Phrase();
        Chunk text9= new Chunk("Cuenta Destino: ", fontParagraphTitle);
        Chunk text10= new Chunk(accountDestination,fontParagraph2);
        phrase5.add(text9);
        phrase5.add(text10);
        cell.setPhrase(phrase5);
        table.addCell(cell);

        Phrase phrase6 = new Phrase();
        Chunk text11 = new Chunk("Titular: ", fontParagraphTitle);
        Chunk text12 = new Chunk(client2.getFirstName()+" "+client2.getLastName(), fontParagraph2);
        phrase6.add(text11);
        phrase6.add(text12);
        cell.setPhrase(phrase6);
        table.addCell(cell);

        document.add(table);

        document.close();
    }

    @Override
    public void exportTransaction(HttpServletResponse response, Client client, Account account, Set<Transaction> transactions) throws IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String currentDatTime= dateFormat.format(new Date());

        document.open();
        Image image = Image.getInstance("src/main/resources/static/bank_78392.png");
        image.scaleAbsolute(50,50);
        image.setAbsolutePosition(120,760);
        document.add(image);
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(20);
        Color colorBank = new Color(135, 2, 35, 204);
        fontTitle.setColor(colorBank);
        Paragraph paragraph = new Paragraph("Minhub Brothers Banks", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(15);
        Paragraph paragraph1 = new Paragraph();
        Chunk text = new Chunk("Estado de cuenta No. ", fontParagraph);
        Chunk text1 = new Chunk(account.getNumber(), fontParagraph);
        paragraph1.add(text);
        paragraph1.add(text1);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph1.setSpacingBefore(15);
        paragraph1.setSpacingAfter(15);
        document.add(paragraph1);

        Paragraph paragraph2 = new Paragraph("Cliente: "+ client.getFirstName()+" "+client.getLastName(), fontParagraph );
        paragraph2.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph2.setSpacingAfter(20);
        document.add(paragraph2);

        Font fontDate = FontFactory.getFont(FontFactory.HELVETICA);
        fontDate.setSize(12);
        Paragraph paragraphDate = new Paragraph("Fecha: "+currentDatTime, fontDate);
        paragraphDate.setSpacingAfter(15);
        document.add(paragraphDate);

        Font fontParagraphTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraphTitle.setSize(12);
        Font fontParagraph2 = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph2.setSize(12);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths( new float[] {1.8f, 5.0f, 1.4f, 2.0f, 2.0f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table, transactions);
        document.add(table);


        document.close();
    }
    private void writeTableHeader(PdfPTable table){
        PdfPCell cell = new PdfPCell();
        Color colorBank = new Color(135, 2, 35, 204);
        cell.setBackgroundColor(colorBank);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("FECHA",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("DESCRIPCION", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("TIPO", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("MONTO", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("BALANCE", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, Set<Transaction> transactions){
        for (Transaction transaction : transactions){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateFormatter = transaction.getDate().format(formatter);
            Locale locale = new Locale("en", "US");
            NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
            String amountFormatCurrency = currency.format(transaction.getAmount());
            String balance = currency.format(transaction.getBalance());
            PdfPCell cell = new PdfPCell();
            cell.setPadding(6);
            Font font = FontFactory.getFont(FontFactory.HELVETICA);
            font.setSize(12);
            cell.setPhrase(new Phrase(dateFormatter, font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(transaction.getDescription(), font));
            table.addCell(cell);
            if (transaction.getType().name().equals("DEBIT")){
                Color colorDebit = new Color(255,0,0);
                Font fontDebit = FontFactory.getFont(FontFactory.HELVETICA);
                fontDebit.setColor(colorDebit);
                fontDebit.setSize(12);
                cell.setPhrase(new Phrase(String.valueOf(transaction.getType()),fontDebit));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(amountFormatCurrency),fontDebit));
                table.addCell(cell);
            } else{
                Color colorCredit = new Color(53,206,53);
                Font fontCredit = FontFactory.getFont(FontFactory.HELVETICA);
                fontCredit.setColor(colorCredit);
                fontCredit.setSize(12);
                cell.setPhrase(new Phrase(String.valueOf(transaction.getType()),fontCredit));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(amountFormatCurrency),fontCredit));
                table.addCell(cell);
            }
            Color colorBalance = new Color(31, 31, 233);
            Font fontBalance = FontFactory.getFont(FontFactory.HELVETICA);
            fontBalance.setColor(colorBalance);
            fontBalance.setSize(12);
            cell.setPhrase(new Phrase(balance,fontBalance));
            table.addCell(cell);
        }
    }
}
