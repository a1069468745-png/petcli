/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.vet;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link VetController}
 */

@WebMvcTest(value = VetController.class,
		includeFilters = @ComponentScan.Filter(value = SpecialtyFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class VetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetRepository vets;

	@MockitoBean
	private SpecialtyRepository specialties;

	private Vet james() {
		Vet james = new Vet();
		james.setFirstName("James");
		james.setLastName("Carter");
		james.setId(1);
		return james;
	}

	private Vet helen() {
		Vet helen = new Vet();
		helen.setFirstName("Helen");
		helen.setLastName("Leary");
		helen.setId(2);
		Specialty radiology = new Specialty();
		radiology.setId(1);
		radiology.setName("radiology");
		helen.addSpecialty(radiology);
		return helen;
	}

	@BeforeEach
	void setup() {
		given(this.vets.findAll()).willReturn(Lists.newArrayList(james(), helen()));
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<Vet>(Lists.newArrayList(james(), helen())));
		given(this.vets.findById(1)).willReturn(Optional.of(helen()));
		given(this.specialties.findSpecialties()).willReturn(Lists.newArrayList(makeRadiology(), makeSurgery()));
		given(this.specialties.findById(eq(1))).willReturn(Optional.of(makeRadiology()));
		given(this.specialties.findById(eq(2))).willReturn(Optional.of(makeSurgery()));

	}

	private Specialty makeRadiology() {
		Specialty specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("radiology");
		return specialty;
	}

	private Specialty makeSurgery() {
		Specialty specialty = new Specialty();
		specialty.setId(2);
		specialty.setName("surgery");
		return specialty;
	}

	@Test
	void showVetListHtml() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));

	}

	@Test
	void showResourcesVetList() throws Exception {
		ResultActions actions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].id").value(1));
	}

	@Test
	void initUpdateVetForm() throws Exception {
		mockMvc.perform(get("/vets/{vetId}/edit", 1))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("vet"))
			.andExpect(model().attributeExists("specialties"))
			.andExpect(content().string(containsString("checked")))
			.andExpect(view().name("vets/createOrUpdateVetForm"));
	}

	@Test
	void processUpdateVetFormSuccess() throws Exception {
		Vet james = james();
		Specialty radiology = makeRadiology();
		james.addSpecialty(radiology);
		given(this.vets.findById(1)).willReturn(Optional.of(james));

		mockMvc.perform(post("/vets/{vetId}/edit", 1).param("specialties", "radiology"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/vets.html"));
	}

}
