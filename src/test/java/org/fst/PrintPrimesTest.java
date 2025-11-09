package org.fst;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests Paramétrés JUnit 5 pour les Chemins de graphe de PrintPrimes
 *
 * Mapping des Nœuds:
 * 0: Entry main
 * 1: if (argv.length != 1)
 * 2: try-catch parseInt
 * 3: Initialize (primes[0]=2, numPrimes=1, curPrime=2)
 * 4: while (numPrimes < n)?
 * 5: curPrime++, isPrime=true
 * 6: for (i=0; i<=numPrimes-1)?
 * 7: if (isDivisible)?
 * 8: break
 * 9: if (isPrime)?
 * 10: Print primes
 */
public class PrintPrimesTest {

    @ParameterizedTest(name = "Chemin {0}: n={1} - {3}")
    @CsvSource({
        // Chemin A: [0,1,2,10] infaisable
        
        // Chemin B: [0,1,2,3,9,2,10] - n=1, déjà 1 premier initialisé
        "B, 1, '[0,1,2,3,9,2,10]', 'n=1: possède déjà 1 nombre premier (2), sortie immédiate de la boucle while'",
        
        // Chemin C: [0,1,2,3,4,5,6,9,2,10] - n=2, trouve 3, for loop entre et sort sans diviseur
        "C, 2, '[0,1,2,3,4,5,6,9,2,10]', 'n=2: trouve 3, la boucle for entre et sort sans trouver de diviseur'",
       
        // Chemin D: [0,1,2,3,4,5,8,9,2,10] - n=2, teste 3, boucle for avec break
        "D, 2, '[0,1,2,3,4,5,8,9,2,10]', 'n=2: similaire à C mais teste le chemin avec break'",
        
        // Chemin E: [0,1,2,3,9,2,3,9,2,10] - n=2, deux itérations de while
        "E, 2, '[0,1,2,3,9,2,3,9,2,10]', 'n=2: deux itérations de la boucle while'",
         
        // Chemin F: [0,1,2,3,9,2,3,4,5,6,9,2,10] - n=3, trouve 2,3,5
        "F, 3, '[0,1,2,3,9,2,3,4,5,6,9,2,10]', 'n=3: trouve 2,3,5 avec entrée dans la boucle for'",
        
        // Chemin G: [0,1,2,3,9,2,3,4,5,8,9,2,10] - n=3, teste avec break dans for
        "G, 3, '[0,1,2,3,9,2,3,4,5,8,9,2,10]', 'n=3: teste le chemin avec break dans la boucle for'",
        
        // Chemin H: [0,1,2,3,4,5,6,7,5,8,9,2,10] - trouve composé, entre dans isDivisible true
        "H, 3, '[0,1,2,3,4,5,6,7,5,8,9,2,10]', 'n=3: rencontre un nombre composé, isDivisible retourne true'",
        
        // Chemin I: [0,1,2,3,4,5,6,7,5,6,9,2,10] - boucle for multiple avec divisibilité
        "I, 4, '[0,1,2,3,4,5,6,7,5,6,9,2,10]', 'n=4: itérations multiples de la boucle for vérifiant la divisibilité'",
        
        // Chemin J: [0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10] - boucle for imbriquée complète
        "J, 5, '[0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10]', 'n=5: boucle for imbriquée avec vérifications multiples de divisibilité'"
    })
    void testControlFlowPaths(String pathId, int n, String pathSequence, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            String[] argv = new String[]{String.valueOf(n)};
            PrintPrimes.main(argv);
            
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);
            
            assertEquals(n, actualCount,
                    String.format("Chemin %s %s: Attendu %d nombres premiers mais obtenu %d. %s",
                            pathId, pathSequence, n, actualCount, description));
            
        } finally {
            System.setOut(originalOut);
        }
    }

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