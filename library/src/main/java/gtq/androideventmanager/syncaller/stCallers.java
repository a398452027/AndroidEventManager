/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.syncaller;


import gtq.androideventmanager.utils.bindCollection.bindHashTreeMap;
import gtq.androideventmanager.utils.bindCollection.bindMapQueue;
import gtq.androideventmanager.utils.bindCollection.bindObj2;

/**
 *
 */
class stCallers {
    public bindObj2<bindMapQueue<String, IBaseSyncCaller>, bindHashTreeMap<String, IBaseSyncCaller>> callers;
    public long ntype;
}
