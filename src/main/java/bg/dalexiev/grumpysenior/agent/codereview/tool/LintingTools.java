package bg.dalexiev.grumpysenior.agent.codereview.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.parameters.P;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LintingTools {

    private final CheckstyleRunner checkstyleRunner;

    private LintingTools(CheckstyleRunner checkstyleRunner) {
        this.checkstyleRunner = checkstyleRunner;
    }

    public static LintingTools newInstance() {
        return new LintingTools(new CheckstyleRunner());
    }

    @Tool(name = "lintJavaCode", description = "Runs Checkstyle on the given Java source code and returns style violations")
    public String lintJavaCode(@ToolParam(description = "Full Java source code to check") String javaCode) throws IOException {
        Path tempFile = Files.createTempFile("submitted-", ".java");
        try {
            Files.writeString(tempFile, javaCode);
            return runCheckstyle(tempFile.toFile());
        } finally {
            Files.deleteIfExists(tempFile); // don't leak temp files across demo runs
        }

    }

    private String runCheckstyle(File javaFile) {
        try {
            List<String> violations = checkstyleRunner.runCheckstyle(javaFile);
            if (violations.isEmpty()) {
                return "No violations found.";
            }
            return String.join("\n", violations);
        } catch (Exception e) {
            return "Checkstyle failed to run: " + e.getMessage();
        }
    }
}
