/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByMultipleTweetsMultipleResults(){
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "alyssa", "BTW i think its not", d3);

        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");

        assertEquals("expected non-singleton list", 2, writtenBy.size());
        assertTrue("expected list to contain multiple tweets", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> tweets = List.of();
        List<Tweet> result = Filter.writtenBy(tweets, "alyssa");
        assertTrue("expected empty result for empty tweet list", result.isEmpty());
    }

    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> result = Filter.writtenBy(List.of(tweet1, tweet2), "maaz");
        assertTrue("expected empty list when no author matches", result.isEmpty());
    }

    @Test
    public void testWrittenByPreservesOrder() {
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "alyssa", "BTW i think its not", d3);

        List<Tweet> result = Filter.writtenBy(List.of(tweet3, tweet2, tweet1), "alyssa");

        assertEquals("Expected preserved order of tweets by alyssa", List.of(tweet3, tweet1), result);
    }

    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testInTimespanMultipleTweetsSingleResults(){
        Instant testStart = Instant.parse("2016-02-17T09:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertEquals("expected a single element list", 1, inTimespan.size());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1)));
    }

    @Test
    public void testInTimespanEmptyList() {
        Timespan span = new Timespan(Instant.parse("2016-02-17T09:00:00Z"),
                Instant.parse("2016-02-17T10:00:00Z"));

        List<Tweet> result = Filter.inTimespan(List.of(), span);

        assertTrue("Expected empty result for empty tweet list", result.isEmpty());
    }

    @Test
    public void testInTimespanBoundaryInclusive() {
        Instant start = Instant.parse("2016-02-17T09:00:00Z");
        Instant end = Instant.parse("2016-02-17T12:00:00Z");

        Tweet tweet3 = new Tweet(3, "maaz", "test", start);
        Tweet tweet4 = new Tweet(4, "ahmad", "test", end);

        Timespan span = new Timespan(start, end);
        List<Tweet> result = Filter.inTimespan(List.of(tweet3, tweet4), span);

        assertEquals("Expected both boundary tweets included", 2, result.size());
        assertTrue("Expected both tweets included", result.containsAll(List.of(tweet3, tweet4)));
    }

    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), List.of("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingSingleMatch() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2),
                List.of("much")
        );

        assertEquals("expected 1 tweet containing 'obama'", 1, result.size());
        assertTrue("expected tweet1 to be in result", result.contains(tweet1));
    }

    @Test
    public void testContainingMultipleWordsSingleMatch() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2),
                Arrays.asList("Much", "laughing")
        );

        assertEquals("expected 1 tweet containing 'Much' or 'laughing'", 1, result.size());
        assertTrue("expected tweet1 to be in result", result.contains(tweet1));
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2),
                Arrays.asList("banana", "unicorn")
        );

        assertTrue("expected no tweets matching given words", result.isEmpty());
    }

    @Test
    public void testContainingEmptyWordsList() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2),
                List.of()
        );

        assertTrue("expected no tweets when words list is empty", result.isEmpty());
    }

    @Test
    public void testContainingEmptyTweetsList() {
        List<Tweet> result = Filter.containing(
                List.of(),
                List.of("talk")
        );

        assertTrue("expected no tweets when tweets list is empty", result.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
