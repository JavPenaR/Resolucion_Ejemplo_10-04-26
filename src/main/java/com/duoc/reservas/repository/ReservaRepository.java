package com.duoc.reservas.repository;

import com.duoc.reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findById(Long id);

    List<Reserva> findBySala(String sala);

    @Query("SELECT r FROM Reserva r WHERE r.fecha = :fecha AND r.sala = :sala AND r.estadoReserva = 'ACTIVA' AND " +
           "((r.horaInicio <= :horaInicio AND r.horaTermino > :horaInicio) OR " +
           "(r.horaInicio < :horaTermino AND r.horaTermino >= :horaTermino) OR " +
           "(r.horaInicio >= :horaInicio AND r.horaTermino <= :horaTermino))")
    List<Reserva> findConflictingReservas(@Param("fecha") LocalDate fecha,
                                         @Param("sala") String sala,
                                         @Param("horaInicio") LocalTime horaInicio,
                                         @Param("horaTermino") LocalTime horaTermino);

    @Query("SELECT DISTINCT r.sala FROM Reserva r WHERE r.fecha = :fecha AND r.estadoReserva = 'ACTIVA'")
    List<String> findSalasOcupadasByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.fecha = :fecha AND r.sala = :sala AND r.estadoReserva = 'ACTIVA' AND " +
           "((r.horaInicio < :horaTermino AND r.horaTermino > :horaInicio))")
    List<Reserva> findReservasOcupadasEnRango(@Param("fecha") LocalDate fecha,
                                             @Param("sala") String sala,
                                             @Param("horaInicio") LocalTime horaInicio,
                                             @Param("horaTermino") LocalTime horaTermino);
}
