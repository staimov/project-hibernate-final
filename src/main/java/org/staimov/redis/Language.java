package org.staimov.redis;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Language {
    private String languageName;
    private Boolean isOfficial;
    private BigDecimal percentage;
}
