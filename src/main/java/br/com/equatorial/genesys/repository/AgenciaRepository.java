package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Agencia;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class AgenciaRepository implements PanacheRepository<Agencia> {

    public PanacheQuery<Agencia> findWithFilters(Long regionalId){


        Map<String, Object> params = new HashMap<>();
        String query = "";

        if (regionalId != null) {
            query += " regional.id = :id";
            params.put("id", regionalId);
        }

        log.info("Executando a query: {}", query);
        return find(query, params);
    }
}
