package br.com.equatorial.genesys.repository;

import java.util.List;

import br.com.equatorial.genesys.model.Group;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {

    public List<Group> findByIds(List<Long> ids) {
        return list("id in (?1)", ids);
    }
}
