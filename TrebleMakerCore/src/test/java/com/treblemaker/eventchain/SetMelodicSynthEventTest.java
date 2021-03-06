package com.treblemaker.eventchain;

import com.treblemaker.SpringConfiguration;
import com.treblemaker.extractors.model.HarmonyExtraction;
import com.treblemaker.model.HiveChord;
import com.treblemaker.model.melodic.BachChorale;
import com.treblemaker.model.progressions.ProgressionDTO;
import com.treblemaker.model.progressions.ProgressionUnit;
import com.treblemaker.model.progressions.ProgressionUnit.ProgressionType;
import com.treblemaker.model.queues.QueueItem;
import com.treblemaker.model.queues.QueueState;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.treblemaker.model.progressions.ProgressionUnit.BarCount.FOUR;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ComponentScan({"com.treblemaker"})
@SpringBootTest(classes = SpringConfiguration.class)
@TestPropertySource(
        locations = "classpath:application-test.properties", properties = {"num_of_alt_melodies=2"})
public class SetMelodicSynthEventTest extends TestCase {

    @Autowired
    private SetMelodicSynthEvent setMelodicSynthEvent;

    private QueueState queueState;

    @Before
    public void setup(){
        String KEY = "c";
        ProgressionUnit pUnitOne = new ProgressionUnit();
        pUnitOne.setKey(KEY);
        pUnitOne.initBars(FOUR.getValue());
        pUnitOne.getProgressionUnitBars().get(0).setChord(new HiveChord("cmaj"));
        pUnitOne.getProgressionUnitBars().get(1).setChord(new HiveChord("cmaj"));
        pUnitOne.getProgressionUnitBars().get(2).setChord(new HiveChord("cmaj"));
        pUnitOne.getProgressionUnitBars().get(3).setChord(new HiveChord("cmaj"));

        QueueItem queueItem = new QueueItem();
        queueItem.setBpm(77);

        ProgressionDTO progressionDTO = new ProgressionDTO();
        progressionDTO.setStructure(Arrays.asList(pUnitOne));

        queueItem.setProgression(progressionDTO);

        queueState = new QueueState();
        queueState.setQueueItem(queueItem);
    }

    @Test
    public void shouldSetMelodies(){
        int NUM_OF_ALT_MELODIES = 2;

        queueState = setMelodicSynthEvent.set(queueState);
        assertThat(queueState.getStructure().get(0).getMelodies()).isNotEmpty();
        assertThat(queueState.getStructure().get(0).getMelodies().size()).isEqualTo(NUM_OF_ALT_MELODIES);
    }

    @Test
    public void shouldFindCorrectMatchLengthAndOffset() {

        BachChorale bOne = new BachChorale();
        bOne.setChord("emin7");
        BachChorale bTwo = new BachChorale();
        bTwo.setChord("emin");
        BachChorale bThree = new BachChorale();
        bThree.setChord("cmaj");
        BachChorale bFour = new BachChorale();
        bFour.setChord("dmin");
        BachChorale bFive = new BachChorale();
        bFive.setChord("cmaj");

        //cmaj (bThree)
        //dmin (bFour)
        //emin7 (bTwo)
        //emin (bTwo)
        //cmaj (bThree)
        //cmaj (bThree)
        //NULL (bbdom7 is NULL)

        HarmonyExtraction hOne = new HarmonyExtraction();
        hOne.setChordname("cmaj");
        HarmonyExtraction hTwo = new HarmonyExtraction();
        hTwo.setChordname("dmin");
        HarmonyExtraction hThree = new HarmonyExtraction();
        hThree.setChordname("emin");
        HarmonyExtraction hFour = new HarmonyExtraction();
        hFour.setChordname("emin");
        HarmonyExtraction hFive = new HarmonyExtraction();
        hFive.setChordname("cmaj");
        HarmonyExtraction hSix = new HarmonyExtraction();
        hSix.setChordname("cmaj");
        HarmonyExtraction hSeven = new HarmonyExtraction();
        hSeven.setChordname("bbdom7");

        List<BachChorale> bachChoralesDb = Arrays.asList(bOne, bTwo, bThree, bFour, bFive);
        List<HarmonyExtraction> extractions = Arrays.asList(hOne, hTwo, hThree, hFour, hFive, hSix, hSeven);

        List<BachChorale> matchingBachChorales = setMelodicSynthEvent.createMatchingChorales(bachChoralesDb, extractions);

        //cmaj (bThree)
        //dmin (bFour)
        //emin7 (bOne)
        //emin (bTwo)
        //cmaj (bThree)
        //cmaj (bThree)
        //NULL (bbdom7 is NULL)

        assertThat(matchingBachChorales).hasSize(7);
        assertThat(matchingBachChorales.get(0).getChord()).isEqualTo(bThree.getChord());
        assertThat(matchingBachChorales.get(1).getChord()).isEqualTo(bFour.getChord());
        assertThat(matchingBachChorales.get(2).getChord()).isEqualTo(bTwo.getChord());
        assertThat(matchingBachChorales.get(3).getChord()).isEqualTo(bTwo.getChord());
        assertThat(matchingBachChorales.get(4).getChord()).isEqualTo(bThree.getChord());
        assertThat(matchingBachChorales.get(5).getChord()).isEqualTo(bThree.getChord());
        assertThat(matchingBachChorales.get(6)).isNull();
    }

    @Test
    public void shouldFindLongestMatch() {

        BachChorale bOne = new BachChorale();
        bOne.setChord("emin7");
        BachChorale bTwo = new BachChorale();
        bTwo.setChord("emin");
        BachChorale bThree = new BachChorale();
        bThree.setChord("cmaj");
        BachChorale bFour = new BachChorale();
        bFour.setChord("dmin");
        BachChorale bFive = new BachChorale();
        bFive.setChord("cmaj");
        BachChorale bSix = new BachChorale();
        bSix.setChord("emin");
        BachChorale bSeven = new BachChorale();
        bSeven.setChord("cmaj");

        // //////////////////////////

        HarmonyExtraction hOne = new HarmonyExtraction();
        hOne.setChordname("emin");
        HarmonyExtraction hTwo = new HarmonyExtraction();
        hTwo.setChordname("cmaj");
        HarmonyExtraction hThree = new HarmonyExtraction();
        hThree.setChordname("dmin");

        List<BachChorale> bachChoralesDb = Arrays.asList(bOne, bTwo, bThree, bFour, bFive, bSix, bSeven);
        List<HarmonyExtraction> extractions = Arrays.asList(hOne, hTwo, hThree);

        SetMelodicSynthEvent.LongestMatchResponse matchResponse = setMelodicSynthEvent.getLongestMatch(bachChoralesDb, extractions);

        assertThat(matchResponse.getMatch()).hasSize(3);
        assertThat(matchResponse.getMatch().get(0).getChord()).isEqualTo(bTwo.getChord());
        assertThat(matchResponse.getMatch().get(1).getChord()).isEqualTo(bThree.getChord());
        assertThat(matchResponse.getMatch().get(2).getChord()).isEqualTo(bFour.getChord());
    }

    @Test
    public void shouldCreateTypeToMatchLengthAndOffset() {

        HarmonyExtraction hOne = new HarmonyExtraction();
        hOne.setChordname("cmaj");
        HarmonyExtraction hTwo = new HarmonyExtraction();
        hTwo.setChordname("dmin");
        HarmonyExtraction hThree = new HarmonyExtraction();
        hThree.setChordname("emin");
        HarmonyExtraction hFour = new HarmonyExtraction();
        hFour.setChordname("emin");
        HarmonyExtraction hFive = new HarmonyExtraction();
        hFive.setChordname("cmaj");
        HarmonyExtraction hSix = new HarmonyExtraction();
        hSix.setChordname("cmaj");
        HarmonyExtraction hSeven = new HarmonyExtraction();
        hSeven.setChordname("bbdom");

        List<HarmonyExtraction> extractionsVerse = Arrays.asList(hThree, hFour, hFive, hSix);
        //emin, emin, cmaj, cmaj
        List<HarmonyExtraction> extractionsChorus = Arrays.asList(hOne, hTwo, hThree, hFour);
        //cmaj, dmin, emin, emin
        List<HarmonyExtraction> extractionsBridge = Arrays.asList(hOne, hTwo, hThree, hSeven);
        //cmaj, dmin, emin, bbdom

        Map<ProgressionType, List<HarmonyExtraction>> typeToExtractMap = new HashMap<>();
        typeToExtractMap.put(ProgressionType.VERSE, extractionsVerse);
        typeToExtractMap.put(ProgressionType.CHORUS, extractionsChorus);
        typeToExtractMap.put(ProgressionType.BRIDGE, extractionsBridge);

        BachChorale bOne = new BachChorale();
        bOne.setChord("emin7");
        BachChorale bTwo = new BachChorale();
        bTwo.setChord("emin");
        BachChorale bThree = new BachChorale();
        bThree.setChord("cmaj");
        BachChorale bFour = new BachChorale();
        bFour.setChord("fdom");
        BachChorale bFive = new BachChorale();
        bFive.setChord("bbdom");

        List<BachChorale> bachChorales = Arrays.asList(bOne, bTwo, bThree, bFour, bFive);
    }

    @Test
    public void shouldCreateCorrectSequenceMap() {

        BachChorale bOne = new BachChorale();
        bOne.setChord("emin7");
        BachChorale bTwo = new BachChorale();
        bTwo.setChord("emin");
        BachChorale bThree = new BachChorale();
        bThree.setChord("cmaj");
        BachChorale bFour = new BachChorale();
        bFour.setChord("dmin7");
        BachChorale bFive = new BachChorale();
        bFive.setChord("emin");
        BachChorale bSix = new BachChorale();
        bSix.setChord("gmaj");

        List<BachChorale> bachChorales = Arrays.asList(bOne, bTwo, bThree, bFour, bFive, bSix);

        Map<ProgressionType, int[]> map = new HashMap<>();
        map.put(ProgressionType.VERSE, new int[]{2, 2});
        map.put(ProgressionType.CHORUS, new int[]{1, 3});
        map.put(ProgressionType.BRIDGE, new int[]{6, 0});

        Map<ProgressionType, List<String>> output = setMelodicSynthEvent.createTypeToChordSequenceMap(map, bachChorales);

        assertThat(output.get(ProgressionType.VERSE)).isEqualTo(Arrays.asList("cmaj", "dmin7"));
        assertThat(output.get(ProgressionType.CHORUS)).isEqualTo(Arrays.asList("dmin7"));
        assertThat(output.get(ProgressionType.BRIDGE)).isEqualTo(Arrays.asList("emin7", "emin", "cmaj", "dmin7", "emin", "gmaj"));
    }
}