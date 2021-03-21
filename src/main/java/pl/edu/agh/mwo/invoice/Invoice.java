package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private static final int exciseFreeDay = 26;
    private static final int exciseFreeMonth = 4;
    private static final int year = 2021;
    private static int InvoicesQuantitySum = 0;

    private int invoiceNumber = 0;
    private LocalDate invoiceDate = LocalDate.now();
    private LocalDate invoiceFuelExciseFreeDay = LocalDate.of(year, exciseFreeMonth, exciseFreeDay);

    // changed to LinkedHashMap to keep order of products added to invoice
    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();

    public Invoice() {
        this.invoiceNumber = InvoicesQuantitySum++;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.put(product, quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));

            if (invoiceDate.isEqual(invoiceFuelExciseFreeDay) && product.getClass() == FuelCanister.class) {
                totalGross = totalGross
                        .add(product.getPriceWithTax().subtract(product.getExciseTax()).multiply(quantity));

            } else {
                totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
            }
        }
        return totalGross;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getResume() {
        StringBuilder builder = new StringBuilder();
        builder.append("Nr faktury: " + this.invoiceNumber + "\n");
        for (Product prod : products.keySet()) {
            builder.append("Nazwa: " + prod.getName() + "\tSztuk: " + products.get(prod) + "\tCena 1szt: "
                    + prod.getPriceWithTax() + "\n");
        }
        builder.append("Liczba pozycji: " + products.size());

        return builder.toString();
    }

    public int getInvoicePositions() {
        return products.size();
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

}
