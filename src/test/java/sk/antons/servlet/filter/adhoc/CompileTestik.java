/*
 *
 */
package sk.antons.servlet.filter.adhoc;

import static java.util.Locale.filter;
import sk.antons.servlet.mimic.MimicServlet;

/**
 *
 * @author antons
 */
public class CompileTestik {

    public static void main(String[] argv) {

        MimicServlet servlet = MimicServlet.builder()
            .inCase()
                .when()
                    .lb()
                       .path().startsWith("/pokus")
                       .and()
                       .method().equals("GET")
                       .and()
                       .schema().equals("http")
                    .rb()
                    .done()
                .process(null)
            .inCase()
                .when()
                    .lb()
                       .path().startsWith("/pokus2")
                       .and()
                       .method().equals("GET")
                       .and()
                       .schema().equals("http")
                    .rb()
                    .done()
                .process(null)
            .build();

        System.out.println(" conf: " + servlet.configurationInfo());

    }
}
