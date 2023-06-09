package StockTracker.entity;

import com.baeldung.soap.ws.client.generated.MNBArfolyamServiceSoap;
import com.baeldung.soap.ws.client.generated.MNBArfolyamServiceSoapImpl;
import jakarta.persistence.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Date;
import java.text.DecimalFormat;

@Entity
@Table(name="exchangerate")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Integer exchangeRateId;

    @Column(name="date")
    private Date dateOfRecorded;

    @Column(name="base_amount")
    private Double baseAmount;

    @Column(name="quantity")
    private Double quantityExchange;

    @Column(name="stock_price")
    private Double stockPrice;

    @Column(name="destination_amount")
    private Double destAmount;

    @Column(name="rate")
    private Double rate;

    @ManyToOne
    @JoinColumn(name="base_currency_id")
    private Currency baseCurrency;

    @ManyToOne
    @JoinColumn(name="destination_currency_id")
    private Currency destinationCurrency;

    @OneToOne(mappedBy = "exchangeRate")
    private Transaction transaction;

    @OneToOne(mappedBy = "openExchangeRate")
    private ClosedTransaction closedTransaction;

    public ExchangeRate(){}

    public ExchangeRate(Date dateOfRecorded, Double quantityExchange, Double stockprice, Currency baseCurrency) {
        this.dateOfRecorded = dateOfRecorded;
        this.quantityExchange = quantityExchange;
        this.stockPrice = stockprice;
        this.baseCurrency = baseCurrency;
        setBaseAmount();
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getDestinationCurrency() {
        return destinationCurrency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setDestinationCurrency(Currency destinationCurrency) {
        this.destinationCurrency = destinationCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Integer getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Integer exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

    public Date getDateOfRecorded() {
        return dateOfRecorded;
    }

    public void setDateOfRecorded(Date dateOfRecorded) {
        this.dateOfRecorded = dateOfRecorded;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount() {
            this.baseAmount = quantityExchange*stockPrice;
    }

    public Double getQuantityExchange() {
        return quantityExchange;
    }

    public void setQuantityExchange(Double quantityExchange) {
        this.quantityExchange = quantityExchange;
    }

    public Double getDestAmount() {
        return destAmount;
    }

    public void setDestAmount() {
        destAmount=PopulateWithMNBData();
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "exchangeRateId=" + exchangeRateId +
                ", dateOfRecorded=" + dateOfRecorded +
                ", baseAmount=" + baseAmount +
                ", quantityExchange=" + quantityExchange +
                ", destAmount=" + destAmount +
                '}';
    }

    public Double PopulateWithMNBData(){

        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
        try {
            String resp = service.getExchangeRates(dateOfRecorded.toString(), dateOfRecorded.toString(),
                    baseCurrency.getCurrencyName()+","+destinationCurrency.getCurrencyName());
            System.out.println(resp);

            rate=parseXml(resp);
            return baseAmount*rate;

        } catch (Exception e) {
            System.err.print(e);
        }
        return null;
    }

    public Double parseXml(String xmlString) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes()));

        Element root = doc.getDocumentElement();
        NodeList dayNodes = root.getElementsByTagName("Day");
        if (dayNodes.getLength() == 0) {
            throw new Exception("No Day element found");
        }

        Element day = (Element) dayNodes.item(0);
        NodeList rateNodes = day.getElementsByTagName("Rate");
        if (rateNodes.getLength() == 0) {
            throw new Exception("No Rate element found");
        }

        Element rate = (Element) rateNodes.item(0);
        String rateString = rate.getTextContent().replace(",", ".");
        return Double.parseDouble(rateString);
    }

}



