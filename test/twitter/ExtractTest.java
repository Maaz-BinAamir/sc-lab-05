/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

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
    
    // getTimespan Tests
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    /*My tests for timespan*/

    @Test
    public void testGetTimespanSingleTweet() {
        Timespan timespan = Extract.getTimespan(List.of(tweet1));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespan_N_Tweets() {
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
        final Instant d4 = Instant.parse("2016-02-18T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "ahmad", "Hot take: University Sucks", d3);
        final Tweet tweet4 = new Tweet(4, "maaz", "Nah, its just a skill issue", d4);

        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d4, timespan.getEnd());
    }

    // getMentionedUsers Tests
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }


    @Test
    public void testGetMentionedUsersOneMentionOneTweet(){
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "ahmad", "Hot take: University Sucks. @Maaz thoughts on it?", d3);

        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet3));

        assertEquals(Set.of("maaz"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsOneTweet(){
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "ahmad", "Hot take: University Sucks. @Maaz and @Ali thoughts on it?", d3);

        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet3));

        assertEquals(Set.of("maaz", "ali"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersMultipleTweet(){
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
        final Instant d4 = Instant.parse("2016-02-17T13:00:00Z");
        final Instant d5 = Instant.parse("2016-02-17T14:00:00Z");

        final Tweet tweet3 = new Tweet(3, "ahmad", "Hot take: University Sucks. @Maaz and @Ali thoughts on it?", d3);
        final Tweet tweet4 = new Tweet(4, "maaz", "Nah, its just a skill issue @Ahmad", d4);
        final Tweet tweet5 = new Tweet(3, "ali", "I'd vouch @maaz", d5);

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet4, tweet5));

        assertEquals(Set.of("maaz", "ahmad", "ali"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersPrecendingAndProceedingCharacters(){
        final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

        final Tweet tweet3 = new Tweet(3, "ahmad", "Hot take: University Sucks. maaz@nust @Ali@! and ok@munneb@nust  thoughts on it?", d3);

        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet3));

        assertEquals(Set.of("ali"), mentionedUsers);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
