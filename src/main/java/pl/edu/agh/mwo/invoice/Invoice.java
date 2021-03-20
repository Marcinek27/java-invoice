package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private static int InvoicesQuantitySum = 0;
    private int invoiceNumber = 0;

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
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
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
                    + prod.getPrice() + "\n");
        }
        builder.append("Liczba pozycji: " + products.size());

        return builder.toString();
    }

    public int getInvoicePositions() {
        return products.size();
    }
}
