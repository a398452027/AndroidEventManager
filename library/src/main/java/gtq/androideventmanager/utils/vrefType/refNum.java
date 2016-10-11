/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.vrefType;

/**
 *
 * @author laodao
 */
public class refNum {

    private long m_nValue;

    public refNum(long n) {
        m_nValue = n;
    }

    public long get() {
        return m_nValue;
    }

    public void set(long n) {
        m_nValue = n;
    }

    public void add(long n) {
        m_nValue += n;
    }

    public void sub(long n) {
        m_nValue -= n;
    }

    public refNum init(long n) {
        m_nValue = n;
        return this;
    }
}
