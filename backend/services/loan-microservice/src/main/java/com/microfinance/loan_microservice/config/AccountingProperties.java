package com.microfinance.loan_microservice.config;

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
        private Integer cash;
        private Integer loanReceivable;
        private Integer interestIncome;
        private Integer moratoryIncome;

        public Integer getCash() {
            return cash;
        }

        public void setCash(Integer cash) {
            this.cash = cash;
        }

        public Integer getLoanReceivable() {
            return loanReceivable;
        }

        public void setLoanReceivable(Integer loanReceivable) {
            this.loanReceivable = loanReceivable;
        }

        public Integer getInterestIncome() {
            return interestIncome;
        }

        public void setInterestIncome(Integer interestIncome) {
            this.interestIncome = interestIncome;
        }

        public Integer getMoratoryIncome() {
            return moratoryIncome;
        }

        public void setMoratoryIncome(Integer moratoryIncome) {
            this.moratoryIncome = moratoryIncome;
        }
    }
}
