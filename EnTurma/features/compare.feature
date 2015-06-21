Feature:
  As a user
  I want to compare two classes
  So i can assess both performance

  Scenario:
    Given I touch the "Comparar Turmas" text
    Then I should see "Primeira Turma:"
    Then I should see "Segunda Turma:"
    Then I should see "COMPARAR"
    And I press the "COMPARAR" button
    And I wait for "Ideb" to appear
    And I wait for 1 second
    Then I touch the "EVASÃO" text
    Then I should see "Evasão"
    And I wait for 1 second
    Then I touch the "RENDIMENTO" text
    Then I should see "Rendimento"
    And I wait for 1 second
    Then I touch the "DISTORÇÃO" text
    Then I should see "Distorção"
    And I wait for 1 second
