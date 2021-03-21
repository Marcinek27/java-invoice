package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.BottleOfWine;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test
    public void testInvoiceHasNumber() {
        int number = invoice.getInvoiceNumber();
        Assert.assertTrue(number > 0);
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumbers() {
        int number = invoice.getInvoiceNumber();
        int number2 = new Invoice().getInvoiceNumber();
        Assert.assertNotEquals(number, number2);
    }

    @Test
    public void testTheSameInvoiceHasTheSameNumber() {
        Assert.assertEquals(invoice.getInvoiceNumber(), invoice.getInvoiceNumber());
    }

    @Test
    public void testTheNextInvoiceIncrementNumberByOne() {
        int number = invoice.getInvoiceNumber();
        int number2 = new Invoice().getInvoiceNumber();
        Assert.assertEquals(number + 1, number2);
    }

    @Test
    public void testEmptyInvoiceResumePrint() {
        StringBuilder builder = new StringBuilder();
        builder.append("Nr faktury: " + invoice.getInvoiceNumber() + "\n");
        builder.append("Liczba pozycji: 0");
        Assert.assertEquals(invoice.getResume(), builder.toString());
    }

    @Test
    public void testInvoiceWithOneProductResumePrint() {
        StringBuilder bulider = new StringBuilder();
        invoice.addProduct(new DairyProduct("Product1", new BigDecimal(6)));
        bulider.append("Nr faktury: " + invoice.getInvoiceNumber() + "\n");
        bulider.append("Nazwa: " + "Product1" + "\tSztuk: " + "1" + "\tCena 1szt: " + "6.48" + "\n");
        bulider.append("Liczba pozycji: 1");
        Assert.assertEquals(bulider.toString(), invoice.getResume());
    }

    @Test
    public void testInvoiceWithManyProductsResumePrint() {
        StringBuilder bulider = new StringBuilder();
        OtherProduct prod1 = new OtherProduct("Product1", new BigDecimal(5));
        OtherProduct prod2 = new OtherProduct("Product2", new BigDecimal(8));
        DairyProduct prod3 = new DairyProduct("Product3", new BigDecimal(16));
        DairyProduct prod4 = new DairyProduct("Product4", new BigDecimal(20));
        invoice.addProduct(prod1);
        invoice.addProduct(prod2, 3);
        invoice.addProduct(prod3, 5);
        invoice.addProduct(prod4);
        bulider.append("Nr faktury: " + invoice.getInvoiceNumber() + "\n");
        bulider.append("Nazwa: " + "Product1" + "\tSztuk: " + "1" + "\tCena 1szt: " + "6.15" + "\n");
        bulider.append("Nazwa: " + "Product2" + "\tSztuk: " + "3" + "\tCena 1szt: " + "9.84" + "\n");
        bulider.append("Nazwa: " + "Product3" + "\tSztuk: " + "5" + "\tCena 1szt: " + "17.28" + "\n");
        bulider.append("Nazwa: " + "Product4" + "\tSztuk: " + "1" + "\tCena 1szt: " + "21.60" + "\n");
        bulider.append("Liczba pozycji: 4");
        Assert.assertEquals(bulider.toString(), invoice.getResume());
    }

    @Test
    public void testInvoiceWithDuplicatedProductsResumePrint() {
        OtherProduct prod1 = new OtherProduct("Product1", new BigDecimal(8));
        DairyProduct prod2 = new DairyProduct("Product2", new BigDecimal(16));
        invoice.addProduct(prod1);
        invoice.addProduct(prod1, 3);
        invoice.addProduct(prod2, 4);
        invoice.addProduct(prod2, 2);
        Assert.assertEquals(invoice.getInvoicePositions(), 2);
    }

    @Test
    public void testInvoiceNormalDayWithExciseProductsResumePrint() {
        OtherProduct prod1 = new OtherProduct("Product1", new BigDecimal(8));
        DairyProduct prod2 = new DairyProduct("Product2", new BigDecimal(16));
        FuelCanister canister1 = new FuelCanister("Fuel canister1", new BigDecimal(200));
        FuelCanister canister2 = new FuelCanister("Fuel canister2", new BigDecimal(300));
        BottleOfWine wine1 = new BottleOfWine("Wine1", new BigDecimal(25));
        BottleOfWine wine2 = new BottleOfWine("Wine2", new BigDecimal(30));
        invoice.addProduct(prod1);
        invoice.addProduct(prod2, 3);
        invoice.addProduct(canister1);
        invoice.addProduct(canister2, 2);
        invoice.addProduct(wine1);
        invoice.addProduct(wine2, 4);
        invoice.setInvoiceDate(LocalDate.of(2021, 3, 21));
        Assert.assertEquals(invoice.getGrossTotal(), new BigDecimal("1268.51"));
    }

    @Test
    public void testInvoiceExciseFreeDayWithExciseProductsResumePrint() {
        OtherProduct prod1 = new OtherProduct("Product1", new BigDecimal(8));
        DairyProduct prod2 = new DairyProduct("Product2", new BigDecimal(16));
        FuelCanister canister1 = new FuelCanister("Fuel canister1", new BigDecimal(200));
        FuelCanister canister2 = new FuelCanister("Fuel canister2", new BigDecimal(300));
        BottleOfWine wine1 = new BottleOfWine("Wine1", new BigDecimal(25));
        BottleOfWine wine2 = new BottleOfWine("Wine2", new BigDecimal(30));
        invoice.addProduct(prod1);
        invoice.addProduct(prod2, 3);
        invoice.addProduct(canister1);
        invoice.addProduct(canister2, 2);
        invoice.addProduct(wine1);
        invoice.addProduct(wine2, 4);
        invoice.setInvoiceDate(LocalDate.of(2021, 4, 26));
        Assert.assertEquals(invoice.getGrossTotal(), new BigDecimal("1251.83"));
    }

}
