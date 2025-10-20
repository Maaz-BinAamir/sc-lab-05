/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    // Test for guessFollowsGraph()
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions(){
        Tweet t1 = new Tweet(1, "ahmed", "uhh this semester :(", d1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(t1));

        assertTrue("no tweets with a mention should return empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention(){
        Tweet t1 = new Tweet(1, "maaz", "uhh this semester :(", d1);
        Tweet t2 = new Tweet(2, "ahmed", "so true @maaz", d2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(t1, t2));

        assertTrue("expected alice as key", followsGraph.containsKey("ahmed"));
        assertTrue("expected alice follows bob", followsGraph.get("ahmed").contains("maaz"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMention(){
        Tweet t1 = new Tweet(1, "ahmed", "so true @maaz. btw great job @muneeb on the project.", d1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(t1));


        Set<String> expected = new HashSet<>(Arrays.asList("maaz", "muneeb"));
        assertEquals("expected both maaz and muneeb followed by ahmed", expected, followsGraph.get("ahmed"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsSameUser() {
        Tweet t1 = new Tweet(1, "ahmed", "hi @maaz", d1);
        Tweet t2 = new Tweet(2, "ahmed", "bye @muneeb", d2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(t1, t2));
        Set<String> expected = new HashSet<>(Arrays.asList("maaz", "muneeb"));

        assertEquals("expected both maaz and muneeb followed by ahmed", expected, followsGraph.get("ahmed"));
    }

    // Test for influencers()
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers(){
        Map<String, Set<String>> followsGraph = Map.of("maaz", Collections.emptySet());
        List<String> influencers = SocialNetwork.influencers(followsGraph);

//        assertEquals("expected maaz only", List.of("maaz"), influencers);
        assertEquals("expected no influencers", 0, influencers.size());
    }

    @Test
    public void testInfluencersSingleInfluencer(){
        Map<String, Set<String>> followsGraph = Map.of("ahmed", Set.of("maaz"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // maaz has 1 follower, ahmed has 0
        assertEquals("there should be only 1 influencer", 1, influencers.size());
        assertEquals("maaz should be first influencer", "maaz", influencers.getFirst());
    }

    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("maaz", Set.of("ahmed", "muneeb"));
        followsGraph.put("ahmed", Set.of("muneeb", "maaz"));
        followsGraph.put("muneeb", Set.of("ahmed"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // ahmed and muneeb both have 2 followers
        assertTrue("ahmed should appear before lower-ranked users",
                influencers.indexOf("ahmed") < influencers.indexOf("maaz"));
        assertTrue("muneeb should appear before lower-ranked users",
                influencers.indexOf("muneeb") < influencers.indexOf("maaz"));
    }

    @Test
    public void testInfluencersTiedInfluence(){
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("maaz", Set.of("ahmed"));
        followsGraph.put("muneeb", Set.of("faisal"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // ahmed and faisal both have 1 follower — should be sorted alphabetically
        assertEquals("expected alphabetical order for tied influencers",
                List.of("ahmed", "faisal"), influencers);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */
}
