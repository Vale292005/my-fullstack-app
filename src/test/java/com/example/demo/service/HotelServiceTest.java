package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HotelServiceTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioHost;

    @BeforeEach
    void setUp() {
        // Limpia datos
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Limpia contexto de seguridad
        SecurityContextHolder.clearContext();

        // Crea usuario HOST
        usuarioHost = new Usuario();
        usuarioHost.setNombre("Usuario Test");
        usuarioHost.setEmail("test@correo.com");
        usuarioHost.setContrasenha(passwordEncoder.encode("1234"));
        usuarioHost.setEdad(LocalDate.of(1990, 1, 1));
        usuarioHost.setTelefono("3001234567");
        usuarioHost.setRol(Rol.ANFITRION);
        usuarioHost.setActivo(true);
        usuarioHost = usuarioRepository.save(usuarioHost);

        // Configura contexto de seguridad
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "test@correo.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOST"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void buscarHoteles_sinParametros_devuelveTodos() {
        // Arrange - Crea hoteles
        Hotel hotel1 = new Hotel();
        hotel1.setNombre("Hotel Test 1");
        hotel1.setDireccion("Calle Falsa 123");
        hotel1.setDescripcion("Hotel de prueba 1");
        hotel1.setUsuario(usuarioHost);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setNombre("Hotel Test 2");
        hotel2.setDireccion("Avenida 456");
        hotel2.setDescripcion("Hotel de prueba 2");
        hotel2.setUsuario(usuarioHost);
        hotelRepository.save(hotel2);

        // Act
        List<HotelDto> result = hotelService.buscarHoteles(null, null);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(HotelDto::nombre)
                .containsExactlyInAnyOrder("Hotel Test 1", "Hotel Test 2");
    }

    @Test
    void buscarHoteles_conDireccion_filtraCorrectamente() {
        // Arrange
        Hotel hotel1 = new Hotel();
        hotel1.setNombre("Hotel Bogotá");
        hotel1.setDireccion("Bogotá Centro");
        hotel1.setDescripcion("Hotel en Bogotá");
        hotel1.setUsuario(usuarioHost);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setNombre("Hotel Medellín");
        hotel2.setDireccion("Medellín Norte");
        hotel2.setDescripcion("Hotel en Medellín");
        hotel2.setUsuario(usuarioHost);
        hotelRepository.save(hotel2);

        // Act
        List<HotelDto> result = hotelService.buscarHoteles("Bogotá", null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Hotel Bogotá");
    }

    @Test
    void buscarHoteles_conNombre_filtraCorrectamente() {
        // Arrange
        Hotel hotel1 = new Hotel();
        hotel1.setNombre("Gran Hotel");
        hotel1.setDireccion("Calle 123");
        hotel1.setDescripcion("Gran hotel");
        hotel1.setUsuario(usuarioHost);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setNombre("Hotel Pequeño");
        hotel2.setDireccion("Calle 456");
        hotel2.setDescripcion("Hotel pequeño");
        hotel2.setUsuario(usuarioHost);
        hotelRepository.save(hotel2);

        // Act
        List<HotelDto> result = hotelService.buscarHoteles(null, "Gran");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Gran Hotel");
    }

    @Test
    void buscarHoteles_conDireccionYNombre_filtraCorrectamente() {
        // Arrange
        Hotel hotel1 = new Hotel();
        hotel1.setNombre("Gran Hotel");
        hotel1.setDireccion("Bogotá Centro");
        hotel1.setDescripcion("Gran hotel en Bogotá");
        hotel1.setUsuario(usuarioHost);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setNombre("Hotel Pequeño");
        hotel2.setDireccion("Bogotá Norte");
        hotel2.setDescripcion("Hotel pequeño");
        hotel2.setUsuario(usuarioHost);
        hotelRepository.save(hotel2);

        Hotel hotel3 = new Hotel();
        hotel3.setNombre("Gran Hotel");
        hotel3.setDireccion("Medellín Centro");
        hotel3.setDescripcion("Gran hotel en Medellín");
        hotel3.setUsuario(usuarioHost);
        hotelRepository.save(hotel3);

        // Act
        List<HotelDto> result = hotelService.buscarHoteles("Bogotá", "Gran");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Gran Hotel");
        assertThat(result.get(0).direccion()).contains("Bogotá");
    }

    @Test
    void crearHotel_guardaHotelCorrectamente() {
        // Arrange
        HotelDto dto = new HotelDto(
                null,
                "Hotel Nuevo",
                "Calle 45",
                "Hotel bonito",
                usuarioHost
        );

        // Act
        hotelService.crearHotel(dto);

        // Assert
        List<Hotel> hoteles = hotelRepository.findAll();
        assertThat(hoteles).hasSize(1);

        Hotel guardado = hoteles.get(0);
        assertThat(guardado.getNombre()).isEqualTo("Hotel Nuevo");
        assertThat(guardado.getDireccion()).isEqualTo("Calle 45");
        assertThat(guardado.getDescripcion()).isEqualTo("Hotel bonito");
        assertThat(guardado.getUsuario().getEmail()).isEqualTo("test@correo.com");
    }

    @Test
    void crearHotel_sinAutenticacion_lanzaExcepcion() {
        // Arrange - Limpia contexto de seguridad
        SecurityContextHolder.clearContext();

        HotelDto dto = new HotelDto(
                null,
                "Hotel Nuevo",
                "Calle 45",
                "Descripción",
                usuarioHost
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> hotelService.crearHotel(dto));
    }

    @Test
    void editarHotel_conIdValido_actualizaHotel() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setNombre("Nombre Viejo");
        hotel.setDireccion("Dirección Vieja");
        hotel.setDescripcion("Descripción Vieja");
        hotel.setUsuario(usuarioHost);
        hotel = hotelRepository.save(hotel);

        HotelDto dto = new HotelDto(
                hotel.getId(),
                "Nuevo Nombre",
                "Nueva Dirección",
                "Descripción Actualizada",
                usuarioHost
        );

        // Act
        hotelService.editarHotel(hotel.getId(), dto);

        // Assert
        Hotel actualizado = hotelRepository.findById(hotel.getId()).orElse(null);
        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(actualizado.getDireccion()).isEqualTo("Nueva Dirección");
        assertThat(actualizado.getDescripcion()).isEqualTo("Descripción Actualizada");
    }

    @Test
    void editarHotel_conIdInvalido_lanzaExcepcion() {
        // Arrange
        HotelDto dto = new HotelDto(
                999L,
                "Nuevo",
                "Dir",
                "Desc",
                usuarioHost
        );

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> hotelService.editarHotel(999L, dto)
        );
        assertThat(exception.getMessage()).isEqualTo("Hotel no encontrado");
    }

    @Test
    void eliminarHotel_existente_eliminaCorrectamente() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setNombre("Hotel a Eliminar");
        hotel.setDireccion("Calle 123");
        hotel.setDescripcion("Hotel temporal");
        hotel.setUsuario(usuarioHost);
        hotel = hotelRepository.save(hotel);

        Long hotelId = hotel.getId();

        // Act
        hotelService.eliminarHotel(hotelId);

        // Assert
        assertThat(hotelRepository.existsById(hotelId)).isFalse();
        assertThat(hotelRepository.findById(hotelId)).isEmpty();
    }

    @Test
    void eliminarHotel_inexistente_lanzaExcepcion() {
        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> hotelService.eliminarHotel(999L)
        );
        assertThat(exception.getMessage()).isEqualTo("Hotel no encontrado");
    }
}