package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IncentivosService {


    public List<MisionDTO> buscarMisionesCompletadas(Long id);

    public List<InsigniaDTO> buscarInsigniasPorId(Long id);

    public MisionDTO buscarMisionActualPorId(Long id);


}
