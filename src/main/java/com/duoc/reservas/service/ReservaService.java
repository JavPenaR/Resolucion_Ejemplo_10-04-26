package com.duoc.reservas.service;

import com.duoc.reservas.exception.ReservaConflictException;
import com.duoc.reservas.exception.ReservaNotFoundException;
import com.duoc.reservas.exception.ValidationException;
import com.duoc.reservas.model.Reserva;
import com.duoc.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Reserva crearReserva(Reserva reserva) {
        validarReserva(reserva);
        
        List<Reserva> conflictos = reservaRepository.findConflictingReservas(
                reserva.getFecha(),
                reserva.getSala(),
                reserva.getHoraInicio(),
                reserva.getHoraTermino()
        );

        if (!conflictos.isEmpty()) {
            throw new ReservaConflictException(
                    "Ya existe una reserva activa para la sala " + reserva.getSala() +
                    " en el horario solicitado el día " + reserva.getFecha()
            );
        }

        reserva.setEstadoReserva(Reserva.EstadoReserva.ACTIVA);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public Reserva buscarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con ID: " + id));
    }

    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        Reserva reservaExistente = buscarReservaPorId(id);
        
        validarReserva(reservaActualizada);
        
        if (!reservaExistente.getSala().equals(reservaActualizada.getSala()) ||
            !reservaExistente.getFecha().equals(reservaActualizada.getFecha()) ||
            !reservaExistente.getHoraInicio().equals(reservaActualizada.getHoraInicio()) ||
            !reservaExistente.getHoraTermino().equals(reservaActualizada.getHoraTermino())) {
            
            List<Reserva> conflictos = reservaRepository.findConflictingReservas(
                    reservaActualizada.getFecha(),
                    reservaActualizada.getSala(),
                    reservaActualizada.getHoraInicio(),
                    reservaActualizada.getHoraTermino()
            );

            conflictos.removeIf(r -> r.getId().equals(id));

            if (!conflictos.isEmpty()) {
                throw new ReservaConflictException(
                        "Ya existe una reserva activa para la sala " + reservaActualizada.getSala() +
                        " en el horario solicitado el día " + reservaActualizada.getFecha()
                );
            }
        }

        reservaExistente.setNombreEstudiante(reservaActualizada.getNombreEstudiante());
        reservaExistente.setCorreoEstudiante(reservaActualizada.getCorreoEstudiante());
        reservaExistente.setSala(reservaActualizada.getSala());
        reservaExistente.setFecha(reservaActualizada.getFecha());
        reservaExistente.setHoraInicio(reservaActualizada.getHoraInicio());
        reservaExistente.setHoraTermino(reservaActualizada.getHoraTermino());
        reservaExistente.setEstadoReserva(reservaActualizada.getEstadoReserva());

        return reservaRepository.save(reservaExistente);
    }

    public void eliminarReserva(Long id) {
        Reserva reserva = buscarReservaPorId(id);
        reservaRepository.delete(reserva);
    }

    public List<Reserva> listarReservasPorSala(String sala) {
        return reservaRepository.findBySala(sala);
    }

    public List<String> consultarSalasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaTermino) {
        List<String> salasOcupadas = reservaRepository.findSalasOcupadasByFecha(fecha);
        
        List<String> todasLasSalas = List.of("SALA-A", "SALA-B", "SALA-C", "SALA-D", "SALA-E");
        
        return todasLasSalas.stream()
                .filter(sala -> !estaSalaOcupadaEnRango(sala, fecha, horaInicio, horaTermino, salasOcupadas))
                .toList();
    }

    private boolean estaSalaOcupadaEnRango(String sala, LocalDate fecha, LocalTime horaInicio, 
                                          LocalTime horaTermino, List<String> salasOcupadas) {
        if (!salasOcupadas.contains(sala)) {
            return false;
        }
        
        List<Reserva> reservasEnRango = reservaRepository.findReservasOcupadasEnRango(
                fecha, sala, horaInicio, horaTermino);
        
        return !reservasEnRango.isEmpty();
    }

    private void validarReserva(Reserva reserva) {
        if (reserva.getNombreEstudiante() == null || reserva.getNombreEstudiante().trim().isEmpty()) {
            throw new ValidationException("El nombre del estudiante es obligatorio");
        }

        if (reserva.getCorreoEstudiante() == null || !EMAIL_PATTERN.matcher(reserva.getCorreoEstudiante()).matches()) {
            throw new ValidationException("El correo electrónico no es válido");
        }

        if (reserva.getSala() == null || reserva.getSala().trim().isEmpty()) {
            throw new ValidationException("La sala es obligatoria");
        }

        if (reserva.getFecha() == null || reserva.getFecha().isBefore(LocalDate.now())) {
            throw new ValidationException("La fecha de reserva debe ser válida y no puede ser anterior a hoy");
        }

        if (reserva.getHoraInicio() == null || reserva.getHoraTermino() == null) {
            throw new ValidationException("Las horas de inicio y término son obligatorias");
        }

        if (!reserva.getHoraTermino().isAfter(reserva.getHoraInicio())) {
            throw new ValidationException("La hora de término debe ser posterior a la hora de inicio");
        }

        if (reserva.getEstadoReserva() == null) {
            throw new ValidationException("El estado de la reserva es obligatorio");
        }
    }
}
