package pl.edu.agh.mwo.invoice;

import static org.junit.Assert.*;
import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
	private static final String PRODUCT_1 = "Product 1";
	private static final String PRODUCT_2 = "Product 2";
	private static final String PRODUCT_3 = "Product 3";
	
	@Before	 
	public void resetNumber() {
		Invoice.resetInvoiceNumber();
	}

	@Test
	public void testEmptyInvoiceHasEmptySubtotal() {
		Invoice invoice = createEmptyInvoice();
		assertBigDecimalsAreEqual(BigDecimal.ZERO, invoice.getTotalNet());
	}

	@Test
	public void testEmptyInvoiceHasEmptyTaxAmount() {
		Invoice invoice = createEmptyInvoice();
		assertBigDecimalsAreEqual(BigDecimal.ZERO, invoice.getTax());
	}

	@Test
	public void testEmptyInvoiceHasEmptyTotal() {
		Invoice invoice = createEmptyInvoice();
		assertBigDecimalsAreEqual(BigDecimal.ZERO, invoice.getTotalGross());
	}

	@Test
	public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 1);
		assertBigDecimalsAreEqual(invoice.getTotalNet(), invoice.getTotalGross());
	}

	@Test
	public void testInvoiceHasProperSubtotalForManyProduct() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 1);
		invoice.addProduct(createOtherProduct(), 1);
		invoice.addProduct(createDairyProduct(), 1);
		assertBigDecimalsAreEqual("259.99", invoice.getTotalNet());
	}

	@Test
	public void testInvoiceHasProperTaxValueForManyProduct() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 1);
		invoice.addProduct(createOtherProduct(), 1);
		invoice.addProduct(createDairyProduct(), 1);
		assertBigDecimalsAreEqual("12.3", invoice.getTax());
	}

	@Test
	public void testInvoiceHasProperTotalValueForManyProduct() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct());
		invoice.addProduct(createOtherProduct());
		invoice.addProduct(createDairyProduct());
		assertBigDecimalsAreEqual("272.29", invoice.getTotalGross());
	}

	@Test
	public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 3); // Subtotal: 599.97
		invoice.addProduct(createOtherProduct(), 2); // Subtotal: 100.00
		invoice.addProduct(createDairyProduct(), 4); // Subtotal: 40.00
		assertBigDecimalsAreEqual("739.97", invoice.getTotalNet());
	}

	@Test
	public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 3); // Total: 599.97
		invoice.addProduct(createOtherProduct(), 2); // Total: 123.00
		invoice.addProduct(createDairyProduct(), 4); // Total: 43.2
		assertBigDecimalsAreEqual("766.17", invoice.getTotalGross());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithZeroQuantity() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithNegativeQuantity() {
		Invoice invoice = createEmptyInvoice();
		invoice.addProduct(createTaxFreeProduct(), -1);
	}
	
	@Test
	public void testInvoiceHasNumber() {
		Invoice invoice = createEmptyInvoice();		
		assertThat(invoice.getInvoiceNumber(),Matchers.greaterThan(0));
		
	}
	@Test
	public void testManyInvoiceHasDifferentNumbers() {
		Invoice invoice1 = createEmptyInvoice();
		Invoice invoice2 = createEmptyInvoice();
		assertNotEquals(invoice1.getInvoiceNumber(),invoice2.getInvoiceNumber());
		
	}
	@Test
	public void testNextInvoiceHasSubsequentNumber() {
		Invoice invoice1 = createEmptyInvoice();
		Invoice invoice2 = createEmptyInvoice();
		assertEquals(1, invoice2.getInvoiceNumber()-invoice1.getInvoiceNumber());
		
	}
	
	@Test 
	public void testPrintedInvoiceHasNumber() {
		Invoice invoice = createEmptyInvoice();
		String printed =invoice.printedVersion();
		String invoiceNumber = String.valueOf(invoice.getInvoiceNumber());
		assertThat(printed, Matchers.containsString(invoiceNumber));

		
	}
	
	@Test 
	public void testPrintedInvoiceHasProductName() {
		Invoice invoice = createEmptyInvoice();
		Product myProduct = new DairyProduct("Mleko", new BigDecimal(50));
		invoice.addProduct(myProduct, 2);
		String printed =invoice.printedVersion();
		assertThat(printed, Matchers.containsString("Mleko"));

		
	}
	
	@Test 
	public void testPrintedInvoiceHasProductType() {
		Invoice invoice = createEmptyInvoice();
		Product myProduct = new DairyProduct("Mleko", new BigDecimal(50));
		invoice.addProduct(myProduct, 2);
		String printed =invoice.printedVersion();
		assertThat(printed, Matchers.containsString("DairyProduct"));
		
	}
	
	
	@Test 
	public void testPrintedInvoiceHasProductPrice() {
		Invoice invoice = createEmptyInvoice();
		Product myProduct = new DairyProduct("Mleko", new BigDecimal(30));
		invoice.addProduct(myProduct, 2);
		String printed =invoice.printedVersion();
		assertThat(printed, Matchers.containsString("30"));
		
	}
	
	
	@Test 
	public void testPrintedInvoiceHasProductAmount() {
		Invoice invoice = createEmptyInvoice();
		Product myProduct = new DairyProduct("Mleko", new BigDecimal(30));
		invoice.addProduct(myProduct, 11115);
		String printed =invoice.printedVersion();
		assertThat(printed, Matchers.containsString("11115"));
		
	}
	
	

	private Invoice createEmptyInvoice() {
		return new Invoice();
	}

	private Product createTaxFreeProduct() {
		return new TaxFreeProduct(PRODUCT_1, new BigDecimal("199.99"));
	}

	private Product createOtherProduct() {
		return new OtherProduct(PRODUCT_2, new BigDecimal("50.0"));
	}

	private Product createDairyProduct() {
		return new DairyProduct(PRODUCT_3, new BigDecimal("10.0"));
	}

	private void assertBigDecimalsAreEqual(String expected, BigDecimal actual) {
		assertEquals(new BigDecimal(expected).stripTrailingZeros(), actual.stripTrailingZeros());
	}

	private void assertBigDecimalsAreEqual(BigDecimal expected, BigDecimal actual) {
		assertEquals(expected.stripTrailingZeros(), actual.stripTrailingZeros());
	}
	
	

}
