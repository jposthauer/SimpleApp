package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataNeo4jTest
class ApplicationTests {

  @Autowired
  Neo4jTemplate template;

  @Autowired
  PersonRepository personRepository;

  private Person person = new Person();
  private List<Person> people;

  private static ServerControls embeddedDatabaseServer;

  @BeforeAll
  static void initializeNeo4j() {

    embeddedDatabaseServer = TestServerBuilders.newInProcessBuilder()
        .newServer();
  }

  @AfterAll
  static void stopNeo4j() {

    embeddedDatabaseServer.close();
  }

  @DynamicPropertySource
  static void neo4jProperties(DynamicPropertyRegistry registry) {

    registry.add("spring.neo4j.uri", () -> "bolt://localhost");
    registry.add("spring.neo4j.authentication.username", () -> "neo4j");
    registry.add("spring.neo4j.authentication.password", () -> "localhost");
  }

  @BeforeEach
  void setup(@Autowired PersonRepository personRepository) {
    person.setFirst("John");
    person.setLast("Doe");
    people = List.of(person);

    personRepository.save(person);
  }

  @Test
  void saveAs() {
    person.setLast("modifiedLast");
    person.setFirst("modifiedFirst");
    template.saveAs(person, TempProjection.class);

    Person saved = personRepository.findById(person.getId()).orElse(null);
    assertThat(saved).isNotNull();
    assertThat(saved.getFirst()).isEqualTo("John");
    assertThat(saved.getLast()).isEqualTo("modifiedLast");
  }

  @Test
  void saveAllAs() {
    person.setLast("modifiedLast");
    person.setFirst("modifiedFirst");
    template.saveAllAs(people, TempProjection.class);

    Person saved = personRepository.findById(person.getId()).orElse(null);
    assertThat(saved).isNotNull();
    assertThat(saved.getFirst()).isEqualTo("John");
    assertThat(saved.getLast()).isEqualTo("modifiedLast");
  }

}
