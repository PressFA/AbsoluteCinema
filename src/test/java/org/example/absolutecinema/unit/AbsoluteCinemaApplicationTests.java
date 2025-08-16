package org.example.absolutecinema.unit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AbsoluteCinemaApplicationTests {

	@Test
	void test() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
		String rawPassword = "test";
		String encodedPassword = encoder.encode(rawPassword);
		System.out.println(encodedPassword);
	}
}
