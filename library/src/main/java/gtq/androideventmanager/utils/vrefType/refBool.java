/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.vrefType;

/**
 *
 * @author laodao
 */
public class refBool {

    private boolean m_nValue;

    public refBool(boolean n) {
        m_nValue = n;
    }

    public boolean get() {
        return m_nValue;
    }

    public void set(boolean n) {
        m_nValue = n;
    }

    public refBool init(boolean n) {
        m_nValue = n;
        return this;
    }

    public void setFasle() {
        m_nValue = false;
    }

    public void setTrue() {
        m_nValue = true;
    }
}
