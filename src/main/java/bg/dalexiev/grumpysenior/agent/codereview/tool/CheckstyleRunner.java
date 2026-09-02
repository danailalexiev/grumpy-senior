package bg.dalexiev.grumpysenior.agent.codereview.tool;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CheckstyleRunner {
    public List<String> runCheckstyle(File javaFile) throws Exception {
        Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        Configuration config = ConfigurationLoader.loadConfiguration(
            "/google_checks.xml",
            new PropertiesExpander(System.getProperties())
        );
        checker.configure(config);

        List<String> violations = new ArrayList<>();
        checker.addListener(new AuditListener() {
            @Override
            public void addError(AuditEvent event) {
                violations.add(event.getLine() + ": " + event.getMessage());
            }
            @Override public void addException(AuditEvent e, Throwable t) {}
            @Override public void auditStarted(AuditEvent e) {}
            @Override public void auditFinished(AuditEvent e) {}
            @Override public void fileStarted(AuditEvent e) {}
            @Override public void fileFinished(AuditEvent e) {}
        });

        checker.process(List.of(javaFile));
        checker.destroy();
        return violations;
    }
}