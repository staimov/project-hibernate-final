package org.staimov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "country", schema = "world")
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "code_2")
    private String alternativeCode;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    private Continent continent;

    private String region;

    @Column(name = "surface_area")
    private BigDecimal surfaceArea;

    @Column(name = "indep_year")
    private Short independenceYear;

    private int population;

    @Column(name = "life_expectancy")
    private BigDecimal lifeExpectancy;

    private BigDecimal gnp;

    @Column(name = "gnpo_id")
    private BigDecimal gnpoId;

    @Column(name = "local_name")
    private String localName;

    @Column(name = "government_form")
    private String governmentForm;

    @Column(name = "head_of_state")
    private String headOfState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capital", referencedColumnName = "id")
    private City capital;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Set<City> cities;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> languages;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Country{");
        sb.append("id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", alternativeCode=").append(alternativeCode);
        sb.append(", name=").append(name);
        sb.append(", continent=").append(continent);
        sb.append(", region=").append(region);
        sb.append(", surfaceArea=").append(surfaceArea);
        sb.append(", independenceYear=").append(independenceYear);
        sb.append(", population=").append(population);
        sb.append(", lifeExpectancy=").append(lifeExpectancy);
        sb.append(", gnp=").append(gnp);
        sb.append(", gnpoId=").append(gnpoId);
        sb.append(", localName=").append(localName);
        sb.append(", governmentForm=").append(governmentForm);
        sb.append(", headOfState=").append(headOfState);
        sb.append(", cities=").append(
                cities.stream()
                        .map(City::getName)
                        .collect(Collectors.toList()));
        sb.append(", capital=").append(capital.getName());
        sb.append(", languages=").append(
                languages.stream()
                    .map(CountryLanguage::getLanguageName)
                    .collect(Collectors.toList()));
        sb.append('}');
        return sb.toString();
    }
}
