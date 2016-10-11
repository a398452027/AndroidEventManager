/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.vrefType;

/**
 *
 * @author laodao
 */
public class refDouble {

    private double m_nValue;

    public refDouble(double n) {
        m_nValue = n;
    }

    public double get() {
        return m_nValue;
    }

    public void set(double n) {
        m_nValue = n;
    }

    public void add(double n) {
        m_nValue += n;
    }

    public void sub(double n) {
        m_nValue -= n;
    }

    public refDouble init(double n) {
        m_nValue = n;
        return this;
    }
}
