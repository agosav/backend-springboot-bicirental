package tpi.alquileres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tpi.alquileres.entities.dto.MonedaRequest;
import tpi.alquileres.services.AlquilerService;

@SpringBootApplication
public class AlquileresApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlquileresApplication.class, args);
	}
}
