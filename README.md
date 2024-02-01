# mimic-servlet

Helper servlet for making simple mock rest services.

You can define paths and content to be returned by those paths. You can define also some somele code to return response, but it is mainly used for static content. Just dummy mock for soap of json rest.  

Example of configuration with [spring boot](https://github.com/antonsjava/sb-sampler/blob/main/src/main/java/sk/antons/sbsampler/rest/RestConf.java#L94)
And with tomcat bellow. I hope this is self descriptive. 

```
package sk.antons.tomcat;

import jakarta.servlet.http.HttpServlet;
import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import sk.antons.servlet.mimic.MimicServlet;


public class TomcatServer {

    public void start() throws Exception {


        int port = 8080;
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("target/tomcat");
        tomcat.setPort(port);
        tomcat.getConnector();

        String contextPath = "/";
        String docBase = new File("target/resources").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        HttpServlet servlet = servlet();
        String servletName = "mimic";
        String urlPattern = "/*";

        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(urlPattern, servletName);

        tomcat.start();
        tomcat.getServer().await();


    }

    private HttpServlet servlet() {
        return MimicServlet.builder()
            .inCase()
                .when()
                    .path().startsWith("/rest/text")
                    .done()
                .process(MimicServlet.processor()
                    .contentType("text/plain")
                    .content("A text to be returned")
                    .build())
            .inCase()
                .when()
                    .path().startsWith("/rest/json")
                    .done()
                .process(MimicServlet.processor()
                    .contentType("application/json")
                    .textContentFromUrl("classpath:samples/ok.json")
                    .build())
            .inCase()
                .when()
                    .path().startsWith("/soap/ok")
                    .and().method().equals("POST")
                    .done()
                .process(MimicServlet.processor()
                    .contentType("application/soap-xml")
                    .textContentFromUrl("classpath:samples/ok.xml")
                    .build())
            .inCase()
                .when()
                    .path().startsWith("/soap/bad")
                    .and().method().equals("POST")
                    .done()
                .process(MimicServlet.processor()
                    .contentType("application/soap-xml")
                    .content(new File("some/xml/in/file/fail.xml"))
                    .build())
            .build();

    }

    public static void main(String[] argv) throws Exception {
        TomcatServer server = new TomcatServer();
        server.start();
    }
}
```
