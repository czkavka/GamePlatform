package projekt.zespolowy.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private EGame name;
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Ranking> rankings = new HashSet<>();

    public Set<Ranking> getRankings() {
        return rankings;
    }

    public void setRankings(Set<Ranking> rankings) {
        this.rankings = rankings;
    }

    public Game() {}

    public Game(EGame name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EGame getName() {
        return name;
    }

    public void setName(EGame name) {
        this.name = name;
    }

}
