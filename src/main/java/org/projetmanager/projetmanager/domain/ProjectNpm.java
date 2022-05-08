package org.projetmanager.projetmanager.domain;

import java.nio.file.Path;
import java.util.List;

public record ProjectNpm(Path packagePath,
                         String name,
                         String version,
                         List<String> commandes) {
}
