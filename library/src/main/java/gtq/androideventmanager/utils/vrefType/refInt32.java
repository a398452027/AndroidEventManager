/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.vrefType;

/**
 *
 * @author laodao
 */
public class refInt32 {

    private int m_nValue;

    public refInt32(int n) {
        m_nValue = n;
    }

    public int get() {
        return m_nValue;
    }

    public void set(int n) {
        m_nValue = n;
    }

    public void add(int n) {
        m_nValue += n;
    }

    public void sub(int n) {
        m_nValue -= n;
    }

    public refInt32 init(int n) {
        m_nValue = n;
        return this;
    }
}
