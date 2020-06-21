package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lists the registered Security providers.
 * @author Stephan Fuhrmann
 */
public class SunProviderListTest {

    @Test
    public void list() {
        Provider[] providers = Security.getProviders();
        Arrays.sort(providers, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        for (Provider provider : providers) {
            System.out.println(provider.getName());
            TreeSet<Provider.Service> sortedServices = new TreeSet<>((o1, o2) -> (o1.getType()+o1.getAlgorithm()).compareTo(o2.getType()+o2.getAlgorithm()));
            sortedServices.addAll(provider.getServices());
            for (Provider.Service service : sortedServices) {
                System.out.println(service.getType()+" - "+service.getClassName()+" - " + service.getAlgorithm());
            }
        }
    }
}
