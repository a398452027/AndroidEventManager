/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.syncaller;

/**
 *
 * @author laodao
 */
public interface ICallbackThread {

    public void addCommand(ICommand command);
    
    public String getName();
}
