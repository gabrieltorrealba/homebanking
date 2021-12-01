package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			//CREATE CLIENTS
			Client client1=new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba1234"));
			Client client2=new Client("Gabriel", "Torrealba", "gabriel.torrealba@gmail.com",passwordEncoder.encode("123456"));
			Client client3=new Client("Admin", "Admin","admin@admin.com", passwordEncoder.encode("admin"));

			//SAVE CLIENTS IN REPOSITORY
			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(client3);

			//CREATE ACCOUNTS
			Account account1= new Account("VIN-25387649",LocalDateTime.now().minusMonths(5), 50000, client1, AccountType.AHORRO);
			Account account2= new Account("VIN-51679438",LocalDateTime.now().plusDays(1), 7500, client1, AccountType.CORRIENTE);
			Account account3= new Account("VIN-31526387",LocalDateTime.now(),5500, client2, AccountType.AHORRO);
			Account account4= new Account("VIN-46375269",LocalDateTime.now().plusDays(1),6000, client2, AccountType.CORRIENTE);


			//CREATE TRANSACTIONS
			Transaction transaction1= new Transaction(TransactionType.CREDIT,2000,"transferencia cta. 6236", LocalDateTime.now().minusMonths(1), account1.getBalance() + 2000);
			account1.addTransactions(transaction1);
			account1.setBalance(account1.getBalance() + 2000);
			accountRepository.save(account1);

			Transaction transaction2= new Transaction(TransactionType.DEBIT,-1000,"compra Mercado Libre #321", LocalDateTime.now().minusDays(15), account1.getBalance() - 1000);
			account1.addTransactions(transaction2);
			account1.setBalance(account1.getBalance() - 1000);
			accountRepository.save(account1);

			Transaction transaction13= new Transaction(TransactionType.CREDIT,1500,"transferencia", LocalDateTime.now().minusDays(10), account1.getBalance() + 1500);
			account1.addTransactions(transaction13);
			account1.setBalance(account1.getBalance() + 1500);
			accountRepository.save(account1);

			Transaction transaction14= new Transaction(TransactionType.DEBIT,-3000,"compra ML", LocalDateTime.now().minusDays(5), account1.getBalance() - 3000);
			account1.addTransactions(transaction14);
			account1.setBalance(account1.getBalance() - 3000);
			accountRepository.save(account1);

			Transaction transaction3= new Transaction(TransactionType.CREDIT,2500,"deposito No. 0123", LocalDateTime.now(), account2.getBalance() + 2500);
			account2.addTransactions(transaction3);
			account2.setBalance(account2.getBalance() + 2500);
			accountRepository.save(account2);

			Transaction transaction4= new Transaction(TransactionType.DEBIT,-500,"pago Farmacity #00157", LocalDateTime.now(), account2.getBalance() -500);
			account2.addTransactions(transaction4);
			account2.setBalance(account2.getBalance() - 500);
			accountRepository.save(account2);

			Transaction transaction5= new Transaction(TransactionType.CREDIT,3000,"transferencia cta. 8254", LocalDateTime.now(), account3.getBalance() +3000);
			account3.addTransactions(transaction5);
			account3.setBalance(account3.getBalance() + 3000);
			accountRepository.save(account3);

			Transaction transaction6= new Transaction(TransactionType.DEBIT,-500,"compra Cafe Martinez #054", LocalDateTime.now(), account3.getBalance() - 500);
			account3.addTransactions(transaction6);
			account3.setBalance(account3.getBalance() - 500);
			accountRepository.save(account3);

			Transaction transaction7= new Transaction(TransactionType.CREDIT,3000,"deposito No. 5402", LocalDateTime.now(), account4.getBalance() + 3000);
			account4.addTransactions(transaction7);
			account4.setBalance(account4.getBalance() + 3000);
			accountRepository.save(account4);

			Transaction transaction8= new Transaction(TransactionType.DEBIT,-1500,"pago Supermercados Coto #2038", LocalDateTime.now(), account4.getBalance() - 1500);
			account4.addTransactions(transaction8);
			account4.setBalance(account4.getBalance() - 1500);
			accountRepository.save(account4);

			//SAVE TRANSACTIONS IN REPOSITORY
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);
			transactionRepository.save(transaction13);
			transactionRepository.save(transaction14);

			//CREATE LOANS
			Loan loan1=new Loan("Hipotecario", 500000d, List.of(12,24,36,48,60),30);
			Loan loan2=new Loan("Personal", 100000d, List.of(6,12,24),25);
			Loan loan3=new Loan("Automotriz", 300000d, List.of(6,12,24,36),35);

			//SAVE LOANS IN REPOSITORY
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			//CREATE CLIENTLOAN (MANY TO MANY)
			ClientLoan clientLoan1=new ClientLoan(400000d, loan1.getPayments().get(4), client1, loan1);
			clientLoanRepository.save(clientLoan1);
			Transaction transaction9 = new Transaction(TransactionType.CREDIT,400000,"Prestamo "+ loan1.getName()+ " aprovado",LocalDateTime.now().minusDays(1), account1.getBalance() + 400000);
			account1.addTransactions(transaction9);
			transactionRepository.save(transaction9);
			account1.setBalance(account1.getBalance() + 400000);
			accountRepository.save(account1);

			ClientLoan clientLoan2=new ClientLoan(50000d, loan2.getPayments().get(1), client1, loan2);
			clientLoanRepository.save(clientLoan2);
			Transaction transaction10 = new Transaction(TransactionType.CREDIT,50000,"Prestamo "+ loan2.getName()+ " aprovado",LocalDateTime.now(), account2.getBalance() + 50000);
			account2.addTransactions(transaction10);
			transactionRepository.save(transaction10);
			account2.setBalance(account2.getBalance() + 50000);
			accountRepository.save(account2);

			ClientLoan clientLoan3=new ClientLoan(100000d, loan2.getPayments().get(2), client2, loan2);
			clientLoanRepository.save(clientLoan3);
			Transaction transaction11 = new Transaction(TransactionType.CREDIT,100000,"Prestamo "+ loan2.getName()+ " aprovado",LocalDateTime.now(), account3.getBalance() + 100000);
			account3.addTransactions(transaction11);
			transactionRepository.save(transaction11);
			account3.setBalance(account3.getBalance() + 100000);
			accountRepository.save(account3);

			ClientLoan clientLoan4=new ClientLoan(200000d,loan3.getPayments().get(3), client2, loan3);
			clientLoanRepository.save(clientLoan4);
			Transaction transaction12 = new Transaction(TransactionType.CREDIT,200000,"Prestamo "+ loan2.getName()+ " aprovado",LocalDateTime.now(), account4.getBalance() + 200000);
			account4.addTransactions(transaction12);
			transactionRepository.save(transaction12);
			account4.setBalance(account4.getBalance() + 200000);
			accountRepository.save(account4);

			//CREATE CARDS
			Card card1= new Card(CardType.DEBIT,CardColor.GOLD,"4026-3248-5687-5942",321,LocalDateTime.now(),LocalDateTime.now().plusYears(5), client1, account1);
			Card card2= new Card(CardType.CREDIT,CardColor.TITANIUM,"4546-3542-7869-4351",763,LocalDateTime.now().minusYears(5).minusMonths(1),LocalDateTime.now().minusMonths(1), client1, account1);
			Card card3= new Card(CardType.DEBIT,CardColor.GOLD,"4025-2323-5768-9832",886,LocalDateTime.now(),LocalDateTime.now().plusYears(5), client2, account3);
			Card card4= new Card(CardType.CREDIT,CardColor.SILVER,"5143-8675-3451-7694",554,LocalDateTime.now(),LocalDateTime.now().plusYears(5), client2, account3);
			Card card5= new Card(CardType.CREDIT,CardColor.SILVER,"5143-8675-3451-7694",554,LocalDateTime.now(),LocalDateTime.now().plusYears(5), client1, account2);

			//SAVE CARDS IN REPOSITORY
			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);
			cardRepository.save(card4);
			cardRepository.save(card5);

		};
	}
}
