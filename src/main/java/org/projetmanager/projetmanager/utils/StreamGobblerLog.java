package org.projetmanager.projetmanager.utils;

import java.time.LocalDateTime;

public record StreamGobblerLog(long id, boolean error, LocalDateTime localDateTime,String msg) {
}
