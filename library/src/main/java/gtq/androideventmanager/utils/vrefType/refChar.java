/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gtq.androideventmanager.utils.vrefType;

/**
 *
 * @author laodao
 */
public class refChar {
    private char m_nValue;

    public refChar(char n){
        m_nValue=n;
    }

    public char get(){
        return m_nValue;
    }

    public void set(char n){
        m_nValue=n;
    }

    public refChar init(char n) {
        m_nValue = n;
        return this;
    }
}
