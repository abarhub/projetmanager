package org.projetmanager.projetmanager.utils;

import org.projetmanager.projetmanager.utils.StreamGobbler;

import java.util.concurrent.Future;

public record ProcessRun(long id,
                         StreamGobbler output, StreamGobbler error,
                         Future<?> outputFutur,
                         Future<?> errorFutur, Process process) {

}
