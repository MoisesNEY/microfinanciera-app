package com.microfinance.loan_microservice.config;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "accounting")
public class AccountingProperties {

    private Service service = new Service();
    private Accounts accounts = new Accounts();

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    public static class Service {
        /**
         * URL del accounting-microservice (puede ser el gateway).
         */
        private String url = "http://api-gateway:8080";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Accounts {
        private UUID cash;
        private UUID loanReceivable;
        private UUID interestIncome;
        private UUID moratoryIncome;

        public UUID getCash() {
            return cash;
        }

        public void setCash(UUID cash) {
            this.cash = cash;
        }

        public UUID getLoanReceivable() {
            return loanReceivable;
        }

        public void setLoanReceivable(UUID loanReceivable) {
            this.loanReceivable = loanReceivable;
        }

        public UUID getInterestIncome() {
            return interestIncome;
        }

        public void setInterestIncome(UUID interestIncome) {
            this.interestIncome = interestIncome;
        }

        public UUID getMoratoryIncome() {
            return moratoryIncome;
        }

        public void setMoratoryIncome(UUID moratoryIncome) {
            this.moratoryIncome = moratoryIncome;
        }
    }
}
