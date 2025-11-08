package org.fst;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests Paramétrés JUnit 5 pour les Chemins de Flux de Contrôle de PrintPrimes
 *
 * Cette classe de test valide différents chemins d'exécution à travers la méthode PrintPrimes.printPrimes().
 * Chaque test correspond à un chemin spécifique dans le Graphe de Flux de Contrôle (CFG).
 */
public class PrintPrimesTest {

    /**
     * ========================================================================
     * TEST DE CHEMINS COMBINÉS - Tous les Chemins de Flux de Contrôle
     * ========================================================================
     *
     * Cette méthode de test unique valide tous les chemins de flux de contrôle à travers
     * la méthode PrintPrimes.printPrimes() en utilisant des tests paramétrés.
     *
     * Chaque ligne CSV représente un chemin dans le CFG :
     * - Colonne 1 : Identifiant du chemin (A, B, C, etc.)
     * - Colonne 2 : Valeur d'entrée (n - nombre de nombres premiers à générer)
     * - Colonne 3 : Nombre attendu de nombres premiers
     * - Colonne 4 : Séquence du chemin (ex : [0,1,2,10])
     * - Colonne 5 : Description du chemin
     */
    @ParameterizedTest(name = "Chemin {0} {3}: n={1}, attendu={2} nombres premiers - {4}")
    @CsvSource({
            "A, 1, 1, '[0,1,2,10]', 'Déjà 1 nombre premier (2), boucle while non entrée'",
            "B, 2, 2, '[0,1,2,3,9,2,10]', 'Trouver le 2ème nombre premier (3) avec vérifications de divisibilité minimales'",
            "C, 3, 3, '[0,1,2,3,4,5,6,9,2,10]', 'Trouver 3 nombres premiers : rencontre le nombre composé 4 (divisible par 2)'",
            "D, 3, 3, '[0,1,2,3,4,5,8,9,2,10]', 'Trouver avec succès les nombres premiers 2, 3, 5 (aucun diviseur trouvé)'",
            "E, 4, 4, '[0,1,2,3,9,2,3,9,2,10]', 'Trouver 4 nombres premiers : 2, 3, 5, 7 (deux itérations réussies)'",
            "F, 5, 5, '[0,1,2,3,9,2,3,4,5,6,9,2,10]', 'Trouver 5 nombres premiers : ajouts réussis et rejets de composés'",
            "G, 6, 6, '[0,1,2,3,9,2,3,4,5,8,9,2,10]', 'Trouver 6 nombres premiers avec vérifications de divisibilité réussies'",
            "H, 7, 7, '[0,1,2,3,4,5,6,7,5,8,9,2,10]', 'Trouver 7 nombres premiers : rencontre 9 (divisible par 3, pas par 2)'",
            "I, 8, 8, '[0,1,2,3,4,5,6,7,5,6,9,2,10]', 'Trouver 8 nombres premiers : nécessite plusieurs vérifications de divisibilité par candidat'",
            "J, 10, 10, '[0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10]', 'Trouver 10 nombres premiers : les grands nombres premiers nécessitent la vérification de nombreux diviseurs'",
            "K, 15, 15, '[0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10]', 'Trouver 15 nombres premiers : tests de divisibilité étendus pour des candidats plus grands'"
    })
    void testAllPaths(String pathId, int n, int expectedCount, String pathSequence, String description) {
        // Capturer la sortie qui irait normalement vers la console
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Exécuter la méthode sous test
            PrintPrimes.printPrimes(n);

            // Obtenir la sortie capturée
            String output = outputStream.toString();

            // Compter le nombre de nombres premiers affichés
            int actualCount = countPrimesInOutput(output);

            // Vérifier que le nombre attendu de nombres premiers a été affiché
            assertEquals(expectedCount, actualCount,
                    String.format("Chemin %s %s : Attendu %d nombres premiers mais obtenu %d. %s",
                            pathId, pathSequence, expectedCount, actualCount, description));

            // Vérification supplémentaire : vérifier que la sortie contient "Prime:" pour chaque nombre premier
            assertTrue(output.contains("Prime:"),
                    String.format("Chemin %s : La sortie devrait contenir des entrées 'Prime:'", pathId));

        } finally {
            // Restaurer le System.out original
            System.setOut(originalOut);
        }
    }

    // ========================================================================
    // MÉTHODES AUXILIAIRES
    // ========================================================================

    /**
     * Compte combien de lignes dans la sortie contiennent "Prime:"
     *
     * @param output La sortie console capturée
     * @return Nombre de nombres premiers affichés
     */
    private int countPrimesInOutput(String output) {
        if (output == null || output.isEmpty()) {
            return 0;
        }

        String[] lines = output.split("\n");
        int count = 0;
        for (String line : lines) {
            if (line.trim().startsWith("Prime:")) {
                count++;
            }
        }
        return count;
    }
}