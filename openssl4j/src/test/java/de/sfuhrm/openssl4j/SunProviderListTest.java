package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Lists the registered Security providers.
 * @author Stephan Fuhrmann
 */
public class SunProviderListTest {

    @Test
    public void list() {
        Provider[] providers = Security.getProviders();
        Arrays.sort(providers, Comparator.comparing(Provider::getName));
        for (Provider provider : providers) {
            System.out.println(provider.getName());
            TreeSet<Provider.Service> sortedServices = new TreeSet<>(Comparator.comparing(o -> (o.getType() + o.getAlgorithm())));
            sortedServices.addAll(provider.getServices());
            for (Provider.Service service : sortedServices) {
                System.out.println(service.getType()+" - "+service.getClassName()+" - " + service.getAlgorithm());
            }
        }
    }
}
