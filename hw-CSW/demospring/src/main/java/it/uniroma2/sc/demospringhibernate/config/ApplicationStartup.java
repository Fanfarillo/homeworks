package it.uniroma2.sc.demospringhibernate.config;

import it.uniroma2.sc.demospringhibernate.control.ControllerCaneEProva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component()
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Autowired
    ControllerCaneEProva controllerCaneEProva;
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        controllerCaneEProva.creazioniDiProva();

    }

}
