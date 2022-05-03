package dev.wcs.nad.tariffmanager.customer.reporting;

import dev.wcs.nad.tariffmanager.customer.model.*;
import dev.wcs.nad.tariffmanager.customer.reporting.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CustomerImporter {

    /*
    IF (TYPE='E')
        NEW EXKLUSIVKUNDE
    ELSE IF (TYPE='V')
        NEW VIKUNDE
    ELSE IF (TYPE='S') AND (AGE < 25)
        NEW JUNIORKUNDE
    ELSE IF (TYPE='S') AND (LAST_PURCHASE < 90 DAYS)
        NEW STANDARDKUNDE_MIT_POTENTIAL
    ELSE
        NEW STANDARDKUNDE_OHNE_POTENTIAL
    XDF57FEO3VQ,Moses Finch,ipsum.ac@quamvel.co.uk,02.01.2000,01.08.2021,S
     */
    public static List<Customer> importKunden(File customerCsv) {
        List<String> customerLines;
        List<Customer> customers = new ArrayList<>();
        try {
            customerLines = Files.readAllLines(customerCsv.toPath());
            for (String customer : customerLines) {
                String[] temp = customer.split(",");
                String id = temp[0];
                String name = temp[1];
                String email = temp[2];
                String birthDay = temp[3];
                String lastBuy = temp[4];
                String type = temp[5];

                try {
                    LocalDate birthDate = DateUtil.convertStringToLocalDate(birthDay);
                    LocalDate lastBuyDate = DateUtil.convertStringToLocalDate(lastBuy);
                    if (type.equals("E")) {
                        SpecialCustomer specialCustomer = new SpecialCustomer(id, name, email, birthDate, lastBuyDate);
                        customers.add(specialCustomer);
                    } else if (type.equals("V")) {
                        VICustomer viCustomer = new VICustomer(id, name, email, birthDate, lastBuyDate);
                        customers.add(viCustomer);
                    } else if (type.equals("S")) {
                        boolean juengerAls25 = Period.between(birthDate, LocalDate.now()).getYears() < 25;
                        boolean letztenKaufInnerhalb90Tage = Period.between(lastBuyDate, LocalDate.now()).getDays() < 90;
                        if (juengerAls25) {
                            JuniorCustomer juniorKunde = new JuniorCustomer(id, name, email, birthDate, lastBuyDate);
                            customers.add(juniorKunde);
                        } else if (letztenKaufInnerhalb90Tage) {
                            StandardCustomerWithPotential potentialCustomer = new StandardCustomerWithPotential(id, name, email, birthDate, lastBuyDate);
                            customers.add(potentialCustomer);
                        } else {
                            StandardCustomerNoPotential noPotentialCustomer = new StandardCustomerNoPotential(id, name, email, birthDate, lastBuyDate);
                            customers.add(noPotentialCustomer);
                        }
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Konnte das Datum für Kunde " + id + " nicht lesen.");
                }

            }
        } catch (IOException e) {
            System.err.println("Could not read file.");
        }
        return customers;
    }
}