package com.treblemaker.selectors;

import com.treblemaker.SpringConfiguration;
import com.treblemaker.model.fx.FXArpeggioWithRating;
import com.treblemaker.selectors.interfaces.ISynthFXSelector;
import com.treblemaker.weighters.enums.WeightClass;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ComponentScan({"com.treblemaker"})
@SpringBootTest(classes = SpringConfiguration.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class SynthFXSelectorTest extends TestCase {

    private ISynthFXSelector synthFXSelector;
    private List<FXArpeggioWithRating> synthFXOptionsA;
    private List<FXArpeggioWithRating> synthFXOptionsB;
    Map<Integer, List<FXArpeggioWithRating>> progressionTypeListMap;

    @Before
    public void setup(){

        FXArpeggioWithRating fxArpeggioDelayA = new FXArpeggioWithRating();
        fxArpeggioDelayA.incrementWeight(WeightClass.OK);

        FXArpeggioWithRating fxArpeggioDelayB = new FXArpeggioWithRating();
        fxArpeggioDelayB.incrementWeight(WeightClass.BAD);

        FXArpeggioWithRating fxArpeggioDelayC = new FXArpeggioWithRating();
        fxArpeggioDelayC.incrementWeight(WeightClass.GOOD);

        FXArpeggioWithRating fxArpeggioDelayD  =  new FXArpeggioWithRating();
        fxArpeggioDelayD.incrementWeight(WeightClass.OK);

        FXArpeggioWithRating fxArpeggioDelayE  =  new FXArpeggioWithRating();
        fxArpeggioDelayE.incrementWeight(WeightClass.OK);

        FXArpeggioWithRating fxArpeggioDelayF = new FXArpeggioWithRating();
        fxArpeggioDelayF.incrementWeight(WeightClass.GOOD);

        synthFXOptionsA = new ArrayList<>();
        synthFXOptionsA.add(fxArpeggioDelayA);
        synthFXOptionsA.add(fxArpeggioDelayB);
        synthFXOptionsA.add(fxArpeggioDelayC);

        synthFXOptionsB = new ArrayList<>();
        synthFXOptionsB.add(fxArpeggioDelayD);
        synthFXOptionsB.add(fxArpeggioDelayE);
        synthFXOptionsB.add(fxArpeggioDelayF);

        progressionTypeListMap = new HashMap<>();
        progressionTypeListMap.put(111, synthFXOptionsA);
        progressionTypeListMap.put(222, synthFXOptionsB);
    }

    @Test
    public void shouldSelectSynthFX(){

    }
}
