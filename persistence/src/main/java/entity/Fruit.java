package entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@Cacheable
public class Fruit extends PanacheEntity {
    @Column(length = 50, unique = true)
    public String name;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public static Uni<List<Fruit>> findAllFruits() {
        return listAll();
    }
}
