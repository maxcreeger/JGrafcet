package com.beetlekhi.grafcet.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

public class GrafcetUtilsTest {

    Grafcet grafcet;

    /*
      _____
     /     \
     |     |
     |   [[1]]
     |     |
     |    -+- 1
     |     |
     |    ===============
     |     |     |     |
     |    [2]   [3]   [4]
    /|\    |\    |     |
     |     | \   |     |
     |     |  \  |     |
     |     |   \ |     |
     |     |   ==========
     |    -+-2     |
     |     |       |
     |     |      -+- 3
     \_____/
      
     */

    @Before
    public void initializeTestData() throws JAXBException {
        File file = new File(getClass().getClassLoader().getResource("unitTestGrafcet.xml").getFile());
        grafcet = GrafcetUtils.readGrafcetFromXML(file);
    }

    @Test
    public void testGetStep() throws JAXBException {
        Step step2 = GrafcetUtils.getStep(grafcet, 2);
        assertNotNull(step2);
        assertNotNull(step2.getNum());
        assertEquals(2, (int) step2.getNum());
    }

    @Test
    public void testGetTransition() throws JAXBException {
        Transition tran2 = GrafcetUtils.getTransition(grafcet, 2);
        assertNotNull(tran2);
        assertNotNull(tran2.getNum());
        assertEquals(2, (int) tran2.getNum());
    }

    @Test
    public void testGetRequiredSteps() throws JAXBException {
        List<Step> steps = GrafcetUtils.getRequiredSteps(grafcet, 3);
        assertNotNull(steps);
        assertFalse(steps.isEmpty());
        assertEquals(3, steps.size());
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 2)));
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 3)));
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 4)));
    }

    @Test
    public void testGetExecutedSteps() throws JAXBException {
        List<Step> steps = GrafcetUtils.getExecutedSteps(grafcet, 1);
        assertNotNull(steps);
        assertFalse(steps.isEmpty());
        assertEquals(3, steps.size());
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 2)));
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 3)));
        assertTrue(steps.contains(GrafcetUtils.getStep(grafcet, 4)));
    }
}
