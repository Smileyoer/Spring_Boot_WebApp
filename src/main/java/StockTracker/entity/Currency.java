package StockTracker.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="currency_id")
    private Integer currencyID;

    @Column(name="currency_name")
    private String currencyName;

    @OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name="base_currency_id")
    private List<ExchangeRate> baseCurrencies;

    @OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name="destination_currency_id")
    private List<ExchangeRate> destinationCurrencies;

    public Currency(){}
    public Currency(Integer currencyID, String currencyName) {
        this.currencyID = currencyID;
        this.currencyName = currencyName;
    }

    public Integer getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(Integer currencyID) {
        this.currencyID = currencyID;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public List<ExchangeRate> getBaseCurrencies() {
        return baseCurrencies;
    }

    public void setBaseCurrencies(List<ExchangeRate> baseCurrencies) {
        this.baseCurrencies = baseCurrencies;
    }

    public List<ExchangeRate> getDestinationCurrencies() {
        return destinationCurrencies;
    }

    public void setDestinationCurrencies(List<ExchangeRate> destinationCurrencies) {
        this.destinationCurrencies = destinationCurrencies;
    }

    public void addExchangeRate (ExchangeRate tempCurrency, boolean isBase){
        if (isBase){
            baseCurrencies.add(tempCurrency);
        }
        else{
            destinationCurrencies.add(tempCurrency);
        }
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currencyID=" + currencyID +
                ", currencyName='" + currencyName + '\'' +
                '}';
    }
}