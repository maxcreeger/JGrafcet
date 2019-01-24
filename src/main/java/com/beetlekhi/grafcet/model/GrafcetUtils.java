package com.beetlekhi.grafcet.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class GrafcetUtils {

    public static Grafcet readGrafcetFromXML(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(com.beetlekhi.grafcet.model.Grafcet.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Grafcet) unmarshaller.unmarshal(file);
    }

    public static Step getStep(Grafcet grafcet, Integer stepNum) {
        return grafcet.getSteps().getStep().stream().filter(step -> step.getNum() == stepNum).findFirst().orElseThrow(() -> new NoSuchElementException());
    }

    public static Transition getTransition(Grafcet grafcet, Integer transitionNum) {
        return grafcet.getTransitions().getTransition().stream().filter(tran -> tran.getNum() == transitionNum).findFirst()
                      .orElseThrow(() -> new NoSuchElementException());
    }

    public static List<Step> getRequiredSteps(Grafcet grafcet, Integer transitionNum) {
        Transition transition = getTransition(grafcet, transitionNum);
        if (transition.getRequiredSteps() == null) {
            return new ArrayList<>();
        } else {
            List<Required> required = transition.getRequiredSteps().getRequired();
            if (required == null) {
                return new ArrayList<>();
            } else {
                return required.stream().map(req -> getStep(grafcet, req.getStep())).collect(Collectors.toList());
            }
        }
    }

    public static List<Step> getExecutedSteps(Grafcet grafcet, Integer transitionNum) {
        Transition transition = getTransition(grafcet, transitionNum);
        if (transition.getRequiredSteps() == null) {
            return new ArrayList<>();
        } else {
            List<Executed> executed = transition.getExecutedSteps().getExecuted();
            if (executed == null) {
                return new ArrayList<>();
            } else {
                return executed.stream().map(exec -> getStep(grafcet, exec.getStep())).collect(Collectors.toList());
            }
        }
    }
}
