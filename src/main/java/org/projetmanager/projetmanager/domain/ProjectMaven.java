package org.projetmanager.projetmanager.domain;

import java.nio.file.Path;

public record ProjectMaven(Path packagePath,
                           String name,
                           String version,
                           String groupId,
                           String artifactId,
                           String groupIdParent,
                           String artifactIdParent,
                           String versionParent) {
}
