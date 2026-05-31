package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;

import java.util.List;

public interface IncentivosService {


    public List<MisionDTO> buscarMisionesCompletadas(Long id);

    public List<InsigniaDTO> buscarInsigniasPorId(Long id);

    public MisionDTO buscarMisionActualPorId(Long id);

    public String publicarYDifundirInsignia(Long id, Insignia insignia);
}
