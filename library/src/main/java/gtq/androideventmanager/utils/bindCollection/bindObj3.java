/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils.bindCollection;

/**
 *
 */
public class bindObj3<O1, O2, O3> {

    private O1 obj1;

    public O1 getObj1() {
        return obj1;
    }

    public void setObj1(O1 obj1) {
        this.obj1 = obj1;
    }
    private O2 obj2;

    public O2 getObj2() {
        return obj2;
    }

    public void setObj2(O2 obj2) {
        this.obj2 = obj2;
    }
    private O3 obj3;

    public O3 getObj3() {
        return obj3;
    }

    public void setObj3(O3 obj3) {
        this.obj3 = obj3;
    }

    public bindObj3(O1 o1, O2 o2, O3 o3) {
        obj1 = o1;
        obj2 = o2;
        obj3 = o3;
    }
}
