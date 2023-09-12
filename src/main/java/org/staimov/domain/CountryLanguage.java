package org.staimov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "country_language", schema = "world")
@Getter
@Setter
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "language")
    private String languageName;

    @Column(name = "is_official", columnDefinition = "bit")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isOfficial;

    private BigDecimal percentage;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CountryLanguage{");
        sb.append("id=").append(id);
        sb.append(", country=").append(country.getName());
        sb.append(", languageName=").append(languageName);
        sb.append(", isOfficial=").append(isOfficial);
        sb.append(", percentage=").append(percentage);
        sb.append('}');
        return sb.toString();
    }
}
