package bot.SincroStock.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import bot.SincroStock.servicios.ServicioEtl;

@Component
public class EtlRunner implements ApplicationRunner {

    private final ServicioEtl etlService;

    public EtlRunner(ServicioEtl etlService) {
        this.etlService = etlService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Carga e indexación estándar al iniciar el contexto
        this.etlService.ejecutarPipelineEtl();
    }
}
