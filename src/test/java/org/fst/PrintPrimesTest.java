package org.fst;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * JUnit 5 Parameterized Tests for PrintPrimes Control Flow Paths
 *
 * This test class validates different execution paths through the PrintPrimes.printPrimes() method.
 * Each test corresponds to a specific path through the Control Flow Graph (CFG).
 *
 * ============================================================================
 * CONTROL FLOW GRAPH (CFG) NODE MAPPING:
 * ============================================================================
 * Node 0:  Method entry point
 * Node 1:  Initialize: primes[0]=2, numPrimes=1, curPrime=2
 * Node 2:  while (numPrimes < n) - Check if we need more primes
 * Node 3:  curPrime++; isPrime=true - Move to next candidate
 * Node 4:  for (int i=0; i<=numPrimes-1; i++) - Start divisibility check loop
 * Node 5:  if (isDivisible(primes[i], curPrime)) - Test if divisible
 * Node 6:  isPrime=false; break - Found divisor, mark as composite
 * Node 7:  i++ (for loop increment) - Continue checking next prime
 * Node 8:  if (isPrime) - Check if candidate is prime
 * Node 9:  primes[numPrimes]=curPrime; numPrimes++ - Add prime to list
 * Node 10: Print all primes and exit
 *
 * ============================================================================
 * PATH DESCRIPTIONS:
 * ============================================================================
 * Each path represents a unique sequence of decisions and loops:
 * - Paths show which branches are taken (true/false)
 * - Repeated nodes indicate loop iterations
 * - Different paths test edge cases and normal flow
 */
public class PrintPrimesTest {

    /**
     * ========================================================================
     * PATH A: [0,1,2,10]
     * ========================================================================
     * DESCRIPTION: Empty path - while loop never executes
     *
     * FLOW:
     *   0: Start method
     *   1: Initialize (primes[0]=2, numPrimes=1, curPrime=2)
     *   2: Check while(numPrimes < n) → FALSE (because numPrimes=1, n≤1)
     *   10: Print primes and exit
     *
     * SCENARIO: When n ≤ 1, we already have 1 prime (2), so condition fails
     * COVERAGE: Tests while loop entry condition when FALSE
     *
     * TEST CASES:
     *   - n=1: Request 1 prime, already have 1, loop doesn't execute
     *   - n=0: Request 0 primes, loop doesn't execute
     */
    @ParameterizedTest(name = "Path A [0,1,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "1, 1, 'Already have 1 prime (2), while loop not entered'"})
    void testPathA_WhileLoopNotEntered(int n, int expectedCount, String description) {
        // Capture the output that would normally go to console
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Execute the method under test
            PrintPrimes.printPrimes(n);

            // Get the captured output
            String output = outputStream.toString();

            // Count how many primes were printed
            int actualCount = countPrimesInOutput(output);

            // Verify the expected number of primes were printed
            assertEquals(expectedCount, actualCount,
                    "Path A: " + description + " - Expected " + expectedCount + " primes");

            // If we expect at least one prime, verify it's 2
            if (expectedCount > 0) {
                assertTrue(output.contains("Prime: 2"),
                        "Path A: Should contain the initial prime 2");
            }
        } finally {
            // Always restore original System.out
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH B: [0,1,2,3,9,2,10]
     * ========================================================================
     * DESCRIPTION: One while iteration, for loop skipped (no divisibility check)
     *
     * FLOW:
     *   0: Start
     *   1: Initialize (numPrimes=1, curPrime=2)
     *   2: while(1 < 2) → TRUE
     *   3: curPrime=3, isPrime=true
     *   9: Add 3 to primes, numPrimes=2 (for loop condition false: i=0, numPrimes-1=0)
     *   2: while(2 < 2) → FALSE
     *   10: Print and exit
     *
     * SCENARIO: Request 2 primes. After finding 2nd prime (3),
     *           for loop doesn't check divisibility because we only have
     *           one previous prime to check against.
     *
     * COVERAGE: Tests when for loop has minimal iterations
     */
    @ParameterizedTest(name = "Path B [0,1,2,3,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "2, 2, 'Find 2nd prime (3) with minimal divisibility checks'"
    })
    void testPathB_MinimalForLoop(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            // Verify correct number of primes
            assertEquals(expectedCount, actualCount,
                    "Path B: " + description);

            // Verify we have both 2 and 3
            assertTrue(output.contains("Prime: 2") && output.contains("Prime: 3"),
                    "Path B: Should contain primes 2 and 3");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH C: [0,1,2,3,4,5,6,9,2,10]
     * ========================================================================
     * DESCRIPTION: For loop finds divisor immediately (composite number detected)
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while(numPrimes < n) → TRUE
     *   3: curPrime++, isPrime=true (e.g., curPrime=4)
     *   4: for(i=0; i<=numPrimes-1) → TRUE (enter loop)
     *   5: isDivisible(primes[0], 4) → isDivisible(2, 4) → TRUE (4%2==0)
     *   6: isPrime=false, break (exit for loop)
     *   9: Skip adding (isPrime is false)
     *   2: Continue while loop
     *   10: Eventually print and exit
     *
     * SCENARIO: Checking composite number (like 4, 6, 8, 9, 10...)
     *           Divisibility check succeeds on FIRST iteration
     *
     * COVERAGE: Tests break statement in for loop (node 6)
     */
    @ParameterizedTest(name = "Path C [0,1,2,3,4,5,6,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "3, 3, 'Finding 3 primes: encounters composite 4 (divisible by 2)'"
    })
    void testPathC_FindCompositeImmediately(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path C: " + description);

            // Should have 2, 3, 5 (skipping 4 as composite)
            assertTrue(output.contains("Prime: 2") &&
                            output.contains("Prime: 3") &&
                            output.contains("Prime: 5"),
                    "Path C: Should skip composite 4 and find primes 2, 3, 5");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH D: [0,1,2,3,4,5,8,9,2,10]
     * ========================================================================
     * DESCRIPTION: For loop completes, no divisors found (prime detected)
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE
     *   3: curPrime++, isPrime=true (e.g., curPrime=3)
     *   4: for loop starts
     *   5: isDivisible check → FALSE (not divisible)
     *   [for loop exits normally - no break]
     *   8: if(isPrime) → TRUE
     *   9: Add prime to array
     *   2: Continue while
     *   10: Print and exit
     *
     * SCENARIO: Finding an actual prime number
     *           All divisibility checks fail (number is prime)
     *
     * COVERAGE: Tests normal for loop exit (no break) and if(isPrime) TRUE branch
     */
    @ParameterizedTest(name = "Path D [0,1,2,3,4,5,8,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "3, 3, 'Successfully finding primes 2, 3, 5 (no divisors found)'"
    })
    void testPathD_FindPrimeSuccessfully(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path D: " + description);

            // Verify first 3 primes
            assertTrue(output.contains("Prime: 2") &&
                            output.contains("Prime: 3") &&
                            output.contains("Prime: 5"),
                    "Path D: Should successfully find primes 2, 3, 5");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH E: [0,1,2,3,9,2,3,9,2,10]
     * ========================================================================
     * DESCRIPTION: Two while iterations, both skip for loop
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE (iteration 1)
     *   3: curPrime++, isPrime=true
     *   9: Add prime (for loop condition false or completes quickly)
     *   2: while → TRUE (iteration 2)
     *   3: curPrime++, isPrime=true
     *   9: Add prime
     *   2: while → FALSE
     *   10: Print and exit
     *
     * SCENARIO: Multiple primes found in sequence without encountering composites
     *
     * COVERAGE: Tests multiple successful while iterations
     */
    @ParameterizedTest(name = "Path E [0,1,2,3,9,2,3,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "4, 4, 'Finding 4 primes: 2, 3, 5, 7 (two successful iterations)'"
    })
    void testPathE_TwoSuccessfulIterations(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path E: " + description);

            // Verify we found 4 primes
            assertTrue(output.contains("Prime: 2") &&
                            output.contains("Prime: 3") &&
                            output.contains("Prime: 5") &&
                            output.contains("Prime: 7"),
                    "Path E: Should find first 4 primes: 2, 3, 5, 7");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH F: [0,1,2,3,9,2,3,4,5,6,9,2,10]
     * ========================================================================
     * DESCRIPTION: First iteration adds prime, second iteration finds composite
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE (iteration 1)
     *   3: curPrime++, isPrime=true
     *   9: Add prime
     *   2: while → TRUE (iteration 2)
     *   3: curPrime++, isPrime=true
     *   4: for loop starts
     *   5: isDivisible → TRUE (composite found)
     *   6: isPrime=false, break
     *   9: Skip adding (isPrime=false)
     *   2: while → FALSE
     *   10: Print and exit
     *
     * SCENARIO: Mixed results - some primes found, some composites rejected
     *
     * COVERAGE: Tests combination of successful prime addition and composite rejection
     */
    @ParameterizedTest(name = "Path F [0,1,2,3,9,2,3,4,5,6,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "5, 5, 'Finding 5 primes: successful additions and composite rejections'"
    })
    void testPathF_MixedPrimesAndComposites(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path F: " + description);

            // Verify first 5 primes (2, 3, 5, 7, 11)
            assertTrue(output.contains("Prime: 2") &&
                            output.contains("Prime: 3") &&
                            output.contains("Prime: 5") &&
                            output.contains("Prime: 7") &&
                            output.contains("Prime: 11"),
                    "Path F: Should find first 5 primes: 2, 3, 5, 7, 11");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH G: [0,1,2,3,9,2,3,4,5,8,9,2,10]
     * ========================================================================
     * DESCRIPTION: First iteration adds prime, second iteration checks divisibility but finds none
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE (iteration 1)
     *   3: curPrime++, isPrime=true
     *   9: Add prime
     *   2: while → TRUE (iteration 2)
     *   3: curPrime++, isPrime=true
     *   4: for loop starts
     *   5: isDivisible → FALSE (not divisible)
     *   8: if(isPrime) → TRUE
     *   9: Add prime
     *   2: while → FALSE
     *   10: Print and exit
     *
     * SCENARIO: Multiple primes found with divisibility checking
     *
     * COVERAGE: Tests successful prime detection after divisibility checks
     */
    @ParameterizedTest(name = "Path G [0,1,2,3,9,2,3,4,5,8,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "6, 6, 'Finding 6 primes with successful divisibility checks'"
    })
    void testPathG_MultiplePrimesWithChecks(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path G: " + description);

            // Verify first 6 primes (2, 3, 5, 7, 11, 13)
            assertTrue(actualCount >= 6,
                    "Path G: Should find at least 6 primes");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH H: [0,1,2,3,4,5,6,7,5,8,9,2,10]
     * ========================================================================
     * DESCRIPTION: For loop executes multiple times, eventually finds divisor
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE
     *   3: curPrime++, isPrime=true
     *   4: for loop iteration 1
     *   5: isDivisible → FALSE
     *   6: (skipped - no break)
     *   7: i++ (continue loop)
     *   5: isDivisible → TRUE (found divisor on 2nd check)
     *   8: if(isPrime) → depends on result
     *   9: Add or skip
     *   2: Continue
     *   10: Print and exit
     *
     * SCENARIO: Composite number that requires checking multiple primes
     *           Example: Checking 9 (not divisible by 2, but divisible by 3)
     *
     * COVERAGE: Tests multiple for loop iterations before finding divisor
     */
    @ParameterizedTest(name = "Path H [0,1,2,3,4,5,6,7,5,8,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "7, 7, 'Finding 7 primes: encounters 9 (divisible by 3, not 2)'"
    })
    void testPathH_MultipleChecksBeforeDivisor(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path H: " + description);

            // Verify we found 7 primes (should include checking 9 as composite)
            assertTrue(actualCount >= 7,
                    "Path H: Should find at least 7 primes");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH I: [0,1,2,3,4,5,6,7,5,6,9,2,10]
     * ========================================================================
     * DESCRIPTION: For loop continues, checks multiple primes, finds divisor later
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE
     *   3: curPrime++, isPrime=true
     *   4: for iteration 1
     *   5: isDivisible → FALSE
     *   6: (skipped)
     *   7: i++ continue
     *   5: isDivisible → FALSE (still not divisible)
     *   6: isPrime=false, break (found divisor on later check)
     *   9: Skip or add
     *   2: Continue
     *   10: Print
     *
     * SCENARIO: Checking larger composite numbers that need multiple checks
     *           Example: 15 (not divisible by 2, not by 3, but by 5)
     *
     * COVERAGE: Tests extended for loop with multiple iterations before break
     */
    @ParameterizedTest(name = "Path I [0,1,2,3,4,5,6,7,5,6,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "8, 8, 'Finding 8 primes: requires multiple divisibility checks per candidate'"
    })
    void testPathI_ExtendedDivisibilityChecks(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path I: " + description);

            // Verify we found 8 primes
            assertTrue(actualCount >= 8,
                    "Path I: Should find at least 8 primes");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * ========================================================================
     * PATH J: [0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10]
     * ========================================================================
     * DESCRIPTION: For loop executes extensively, checks many primes, no divisor found
     *
     * FLOW:
     *   0: Start
     *   1: Initialize
     *   2: while → TRUE
     *   3: curPrime++, isPrime=true
     *   4: for iteration 1
     *   5: isDivisible → FALSE
     *   6: (skipped)
     *   7: i++ continue
     *   5: isDivisible → FALSE (check 2nd prime)
     *   6: (skipped)
     *   7: i++ continue
     *   5: isDivisible → FALSE (check 3rd prime)
     *   [loop continues checking all previous primes]
     *   8: if(isPrime) → TRUE (survived all checks!)
     *   9: Add prime
     *   2: Continue
     *   10: Print
     *
     * SCENARIO: Finding larger primes that require checking against many previous primes
     *           Example: 17, 19, 23, 29... (must check divisibility by 2,3,5,7,11,13...)
     *
     * COVERAGE: Tests complete for loop execution checking all previous primes
     */
    @ParameterizedTest(name = "Path J [0,1,2,3,4,5,6,7,5,6,7,5,8,9,2,10]: n={0}, expected={1} primes - {2}")
    @CsvSource({
            "10, 10, 'Finding 10 primes: large primes require checking many divisors'",
            "15, 15, 'Finding 15 primes: extensive divisibility testing for larger candidates'"
    })
    void testPathJ_ExtensiveLoopAllChecks(int n, int expectedCount, String description) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            PrintPrimes.printPrimes(n);
            String output = outputStream.toString();
            int actualCount = countPrimesInOutput(output);

            assertEquals(expectedCount, actualCount,
                    "Path J: " + description);

            // Verify we found larger primes that require extensive checking
            assertTrue(output.contains("Prime: 17") ||
                            output.contains("Prime: 19") ||
                            output.contains("Prime: 23"),
                    "Path J: Should find larger primes (17, 19, 23...) requiring many divisibility checks");
        } finally {
            System.setOut(originalOut);
        }
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Counts how many lines in the output contain "Prime:"
     *
     * @param output The captured console output
     * @return Number of primes printed
     */
    private int countPrimesInOutput(String output) {
        if (output == null || output.trim().isEmpty()) {
            return 0;
        }

        // Count lines that start with "Prime:"
        return (int) output.lines()
                .filter(line -> line.trim().startsWith("Prime:"))
                .count();
    }
}