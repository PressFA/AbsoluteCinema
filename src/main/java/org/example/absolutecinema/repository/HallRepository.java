package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.hall.FullInfoHallDto;
import org.example.absolutecinema.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Long> {
    List<FullInfoHallDto> findFullInfoHallsBy();
}
