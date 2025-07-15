package kr.co.mathrank.app.api.common.exception;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = ApiExceptionOccuringController.class)
class ApiExceptionHandlerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void validation_예외발생_시_응답() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/error"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(content().string(containsString("1000")));
	}

	@Test
	void 서블릿_바인딩_에러_시_응답() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/require-argument"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(content().string(containsString("1001")))
			.andDo(MockMvcResultHandlers.print());
	}
}
