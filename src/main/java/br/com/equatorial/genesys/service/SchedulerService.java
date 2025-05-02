package br.com.equatorial.genesys.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Set;


@ApplicationScoped
@AllArgsConstructor
public class SchedulerService {

    private final TicketsService ticketsService;

    private final OAuthService authService;
    private final EmailService emailService;
    
    @ConfigProperty(name = "config.scheduler.timeToRun")
    private final String timeToRun = "1h";

    @Scheduled(every = timeToRun)
    public void slaScheduler(){
//        ticketsService.atualizarSla();
    }

    @Scheduled(cron = "0 0 8 ? * MON-FRI")
    public void slaCloseScheduler(){
        ticketsService.fecharSla();
    }
}
