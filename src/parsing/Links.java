//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.07.31 at 08:16:11 AM CEST 
//


package parsing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}step2transition"/>
 *         &lt;element ref="{}divergence"/>
 *         &lt;element ref="{}convergence"/>
 *         &lt;element ref="{}transtion2step"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "step2TransitionOrDivergenceOrConvergence"
})
@XmlRootElement(name = "links")
public class Links {

    @XmlElements({
        @XmlElement(name = "step2transition", type = Step2Transition.class),
        @XmlElement(name = "divergence", type = Divergence.class),
        @XmlElement(name = "convergence", type = Convergence.class),
        @XmlElement(name = "transtion2step", type = Transtion2Step.class)
    })
    protected List<Object> step2TransitionOrDivergenceOrConvergence;

    /**
     * Gets the value of the step2TransitionOrDivergenceOrConvergence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the step2TransitionOrDivergenceOrConvergence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStep2TransitionOrDivergenceOrConvergence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Step2Transition }
     * {@link Divergence }
     * {@link Convergence }
     * {@link Transtion2Step }
     * 
     * 
     */
    public List<Object> getStep2TransitionOrDivergenceOrConvergence() {
        if (step2TransitionOrDivergenceOrConvergence == null) {
            step2TransitionOrDivergenceOrConvergence = new ArrayList<Object>();
        }
        return this.step2TransitionOrDivergenceOrConvergence;
    }

}
