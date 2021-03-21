package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    private final BigDecimal exciseTax;

    protected Product(String name, BigDecimal price, BigDecimal tax, BigDecimal exciseTax) {
        if (name == null || name.equals("") || price == null || tax == null || tax.compareTo(new BigDecimal(0)) < 0
                || price.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
        this.exciseTax = exciseTax;
    }

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        this(name, price, tax, BigDecimal.ZERO);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getExciseTax() {
        return exciseTax;
    }

    public BigDecimal getPriceWithTax() {
        return price.multiply(taxPercent).add(price).add(exciseTax);
    }
}
