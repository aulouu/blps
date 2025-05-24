package itmo.blps.camunda;

import itmo.blps.security.BeanProvider;
import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.PreUndeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.JakartaServletProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;

@ProcessApplication("mainProcessBLPS")
public class BlpsProcessApplication extends ServletProcessApplication {
    @PostDeploy
    public void startProcess(ProcessEngine processEngine) {
        BeanProvider.getBean(BeanProvider.class).setProcessEngine(processEngine);
        System.out.println("Post Deploy " + processEngine);
    }

    @PreUndeploy
    public void remove() {
        System.out.println("Pre Undeploy");
    }
}
