//package com.example.demo.model;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//public class ReadingRepo {
//
//    private static final List<Reading> readings = List.of(
//            new Reading(
//                    "The Fox and the Grapes",
//                    "A hungry fox saw some fine bunches of grapes hanging from a vine. "
//                            + "He did his best to reach them, but they were just out of reach. "
//                            + "Finally, he gave up and said, 'They're probably sour anyway.'",
//                    List.of(
//                            new Question("What fruit did the fox want?", Arrays.asList("Apples", "Grapes", "Pears"), "Grapes"),
//                            new Question("Why did the fox give up?", Arrays.asList("Grapes Too High","Apples Too High","Too low"), "Grapes Too high")
//                    ),
//                    1
//            ),
//            new Reading(
//                    "The Tortoise and the Hare",
//                    "A hare mocked a slow-moving tortoise. The tortoise challenged the hare to a race. "
//                            + "The hare ran far ahead, then took a nap, confident of winning. "
//                            + "The tortoise kept going slowly and steadily, eventually winning the race.",
//                    List.of(
//                            new Question("Who won the race?", Arrays.asList("Hare", "Tortoise"), "Tortoise"),
//                            new Question("What is the moral of the story?",Arrays.asList("Slow and Steady wins","Fast and crazy Wins","Boring story") ,"Slow and Steady wins")
//                    ),
//                    1
//            ),
//            new Reading(
//                    "The Lion and the Mouse",
//                    "A lion caught a small mouse. The mouse begged for its life, promising to help someday. "
//                            + "The lion laughed but let it go. Later, hunters trapped the lion in a net. "
//                            + "The mouse chewed through the ropes and freed the lion.",
//                    List.of(
//                            new Question("Who helped the lion escape?", Arrays.asList("The hunters", "The mouse", "Another lion"), "The mouse"),
//                            new Question("What lesson does this story teach?",Arrays.asList("Being useful","Being reliable","Being angry"), "Being useful")
//                    ),
//                    1
//            ),
//            new Reading(
//                    "The Boy Who Cried Wolf",
//                    "A shepherd boy liked to play tricks. He repeatedly cried 'Wolf!' when there was none, "
//                            + "and the villagers rushed to help. Later, when a wolf truly appeared, no one believed him, "
//                            + "and his sheep were eaten.",
//                    List.of(
//                            new Question("What did the boy lie about?", Arrays.asList("Seeing a wolf", "Losing sheep", "Falling ill"), "Seeing a wolf"),
//                            new Question("Why didn’t the villagers help him at the end?",Arrays.asList("They doubted","They believed","They hated"), "They doubted")
//                    ),
//                    1
//            ),
//            new Reading(
//                    "The Ant and the Grasshopper",
//                    "All summer long, the ant worked hard gathering food, while the grasshopper sang and played. "
//                            + "When winter came, the grasshopper had nothing to eat, while the ant lived comfortably "
//                            + "on its stored supplies.",
//                    List.of(
//                            new Question("What did the ant do during summer?", Arrays.asList("Played music", "Gathered food", "Slept"), "Gathered food"),
//                            new Question("What happened to the grasshopper in winter?",Arrays.asList("No food","Too much food", "Sleeping"), "No food")
//                    ),
//                    1
//            )
//    );
//
//    public static Reading getRandomReading() {
//        Random random = new Random();
//        return readings.get(random.nextInt(readings.size()));
//    }
//
//    public static List<Reading> getAllReadings() {
//        return readings;
//    }
//}