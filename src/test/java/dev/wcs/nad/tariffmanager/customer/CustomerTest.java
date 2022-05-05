package dev.wcs.nad.tariffmanager.customer;

import dev.wcs.nad.tariffmanager.customer.model.Customer;
import dev.wcs.nad.tariffmanager.customer.model.SpecialCustomer;
import dev.wcs.nad.tariffmanager.customer.model.StandardCustomerNoPotential;
import dev.wcs.nad.tariffmanager.customer.model.VICustomer;
import dev.wcs.nad.tariffmanager.customer.reporting.CustomerImporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerTest {

    @Test
    public void shouldCreateDifferentCustomersWithDifferentDiscounts() {
        Customer alfred22 = new VICustomer("1", "Alfred", "alfred@web.de", LocalDate.now().minusYears(22), LocalDate.now().minusDays(100));
        Customer achim24 = new SpecialCustomer("1", "Achim", "achim@web.de", LocalDate.now().minusYears(24), LocalDate.now().minusDays(100));
        Customer egon54 = new StandardCustomerNoPotential("1", "Egon", "egon@web.de", LocalDate.now().minusYears(54), LocalDate.now().minusDays(100));

        assertThat(alfred22.getEmail()).isEqualTo("alfred@web.de");
        assertThat(alfred22).isInstanceOf(VICustomer.class);
        assertThat(alfred22).isInstanceOf(Customer.class);

        assertThat(alfred22.calculateDiscountedPrice(100)).isEqualTo(90);
        assertThat(achim24.calculateDiscountedPrice(100)).isEqualTo(95);
        assertThat(egon54.calculateDiscountedPrice(100)).isEqualTo(100);
    }

    @Test
    public void shouldImplementFilterOnGenericPropertiesLikeName() {
        Customer alfred22 = new VICustomer("1", "Alfred", "alfred@web.de", LocalDate.now().minusYears(22), LocalDate.now().minusDays(100));
        Customer achim24 = new SpecialCustomer("1", "Achim", "achim@web.de", LocalDate.now().minusYears(24), LocalDate.now().minusDays(100));
        Customer egon54 = new StandardCustomerNoPotential("1", "Egon", "egon@web.de", LocalDate.now().minusYears(54), LocalDate.now().minusDays(100));
        List<Customer> customers = List.of(alfred22, achim24, egon54);
        List<Customer> customersOlderThan22 = customers.stream().filter(customer -> customer.getBirthDate().plusYears(22).isBefore(LocalDate.now())).collect(Collectors.toList());
        assertThat(customersOlderThan22).hasSize(2);
    }

    @Test
    public void shouldImplementLogicOnBaseMethods() {
        Customer alfred22 = new VICustomer("1", "Alfred", "alfred@web.de", LocalDate.now().minusYears(22), LocalDate.now().minusDays(100));
        Customer achim24 = new SpecialCustomer("1", "Achim", "achim@web.de", LocalDate.now().minusYears(24), LocalDate.now().minusDays(100));
        Customer egon54 = new StandardCustomerNoPotential("1", "Egon", "egon@web.de", LocalDate.now().minusYears(54), LocalDate.now().minusDays(100));
        List<Customer> customers = List.of(alfred22, achim24, egon54);
        // This collection is filtered using a conventional for-loop
        for (Customer customer : customers) {
            if (customer instanceof VICustomer) {
                System.out.println("Mail of VICustomer: " + customer.getEmail());
            }
        }
    }

    @Test
    public void shouldImportCsvToCustomerObjectModel() {
        File customerCsv = new File("src/test/resources/testdata/customer.csv");
        List<Customer> importedCustomers = CustomerImporter.importCustomers(customerCsv);
        assertThat(importedCustomers).hasSize(99);
    }

}