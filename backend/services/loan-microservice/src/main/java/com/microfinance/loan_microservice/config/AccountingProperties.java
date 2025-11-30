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
        /**
         * Nombre lógico de la cuenta de caja/banco (ej: "CASH_ACCOUNT").
         * Se consulta al accounting-microservice para obtener el UUID.
         * El nombre lógico es independiente del código numérico y puede variar por empresa/ambiente.
         */
        private String cash;
        
        /**
         * Nombre lógico de la cuenta de préstamos por cobrar (ej: "LOAN_RECEIVABLE_ACCOUNT").
         * Se consulta al accounting-microservice para obtener el UUID.
         * El nombre lógico es independiente del código numérico y puede variar por empresa/ambiente.
         */
        private String loanReceivable;
        
        /**
         * Nombre lógico de la cuenta de ingresos por intereses (ej: "INTEREST_INCOME_ACCOUNT").
         * Se consulta al accounting-microservice para obtener el UUID.
         * El nombre lógico es independiente del código numérico y puede variar por empresa/ambiente.
         */
        private String interestIncome;
        
        /**
         * Nombre lógico de la cuenta de ingresos por mora (ej: "MORATORY_INCOME_ACCOUNT").
         * Se consulta al accounting-microservice para obtener el UUID.
         * El nombre lógico es independiente del código numérico y puede variar por empresa/ambiente.
         */
        private String moratoryIncome;

        public String getCash() {
            return cash;
        }

        public void setCash(String cash) {
            this.cash = cash;
        }

        public String getLoanReceivable() {
            return loanReceivable;
        }

        public void setLoanReceivable(String loanReceivable) {
            this.loanReceivable = loanReceivable;
        }

        public String getInterestIncome() {
            return interestIncome;
        }

        public void setInterestIncome(String interestIncome) {
            this.interestIncome = interestIncome;
        }

        public String getMoratoryIncome() {
            return moratoryIncome;
        }

        public void setMoratoryIncome(String moratoryIncome) {
            this.moratoryIncome = moratoryIncome;
        }
    }
}
